package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.ContratoRequest;
import br.pucminas.aluguelcarros.exception.AcessoNegadoException;
import br.pucminas.aluguelcarros.model.*;
import br.pucminas.aluguelcarros.repository.AgenteRepository;
import br.pucminas.aluguelcarros.repository.AutomovelRepository;
import br.pucminas.aluguelcarros.repository.ContratoRepository;
import br.pucminas.aluguelcarros.repository.PedidoAluguelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContratoServiceTest {

    @Mock
    private ContratoRepository contratoRepository;
    @Mock
    private PedidoAluguelRepository pedidoRepository;
    @Mock
    private AgenteRepository agenteRepository;
    @Mock
    private AutomovelRepository automovelRepository;
    @Mock
    private PedidoService pedidoService;

    private ContratoService service;
    private PedidoAluguel pedido;

    @BeforeEach
    void configurar() {
        service = new ContratoService(contratoRepository, pedidoRepository, agenteRepository,
                automovelRepository, pedidoService);

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");

        Automovel automovel = new Automovel();
        automovel.setId(10L);
        automovel.setMarca("Fiat");
        automovel.setModelo("Argo");
        automovel.setPlaca("ABC1D23");

        pedido = new PedidoAluguel();
        pedido.setId(20L);
        pedido.setCliente(cliente);
        pedido.setAutomovel(automovel);
        pedido.setDataInicio(LocalDate.now().plusDays(5));
        pedido.setDataFim(LocalDate.now().plusDays(8));
        pedido.setStatus(StatusPedido.APROVADO);

        when(pedidoService.buscarEntidade(pedido.getId())).thenReturn(pedido);
        when(contratoRepository.existsByPedidoId(pedido.getId())).thenReturn(false);
    }

    @Test
    void empresaNaoPodeConcederCredito() {
        Agente empresa = agente(2L, TipoAgente.EMPRESA);
        when(agenteRepository.findById(empresa.getId())).thenReturn(Optional.of(empresa));
        ContratoRequest dados = new ContratoRequest(TipoProprietario.CLIENTE, 3000.0,
                3L, 3000.0, 12, 1.5);

        AcessoNegadoException erro = assertThrows(AcessoNegadoException.class,
                () -> service.executar(pedido.getId(), dados, empresa));

        assertTrue(erro.getMessage().contains("Somente um banco"));
        verify(contratoRepository, never()).save(any());
    }

    @Test
    void bancoNaoPodeConcederCreditoEmNomeDeOutroBanco() {
        Agente bancoLogado = agente(2L, TipoAgente.BANCO);
        Agente outroBanco = agente(3L, TipoAgente.BANCO);
        when(agenteRepository.findById(bancoLogado.getId())).thenReturn(Optional.of(bancoLogado));
        when(agenteRepository.findById(outroBanco.getId())).thenReturn(Optional.of(outroBanco));
        ContratoRequest dados = new ContratoRequest(TipoProprietario.CLIENTE, 3000.0,
                outroBanco.getId(), 3000.0, 12, 1.5);

        AcessoNegadoException erro = assertThrows(AcessoNegadoException.class,
                () -> service.executar(pedido.getId(), dados, bancoLogado));

        assertTrue(erro.getMessage().contains("banco autenticado"));
        verify(contratoRepository, never()).save(any());
    }

    private Agente agente(Long id, TipoAgente tipo) {
        Agente agente = new Agente();
        agente.setId(id);
        agente.setNome(tipo == TipoAgente.BANCO ? "Banco Teste " + id : "Empresa Teste");
        agente.setTipo(tipo);
        return agente;
    }
}
