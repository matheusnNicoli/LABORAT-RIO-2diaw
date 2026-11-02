package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.AvaliacaoRequest;
import br.pucminas.aluguelcarros.dto.PedidoRequest;
import br.pucminas.aluguelcarros.exception.RegraNegocioException;
import br.pucminas.aluguelcarros.model.*;
import br.pucminas.aluguelcarros.repository.AgenteRepository;
import br.pucminas.aluguelcarros.repository.ClienteRepository;
import br.pucminas.aluguelcarros.repository.PedidoAluguelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoAluguelRepository pedidoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private AgenteRepository agenteRepository;
    @Mock
    private AutomovelService automovelService;

    private PedidoService service;
    private Cliente cliente;
    private Agente agente;
    private Automovel automovel;

    @BeforeEach
    void configurar() {
        service = new PedidoService(pedidoRepository, clienteRepository, agenteRepository, automovelService);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");

        agente = new Agente();
        agente.setId(2L);
        agente.setNome("Empresa Teste");
        agente.setTipo(TipoAgente.EMPRESA);

        automovel = new Automovel();
        automovel.setId(10L);
        automovel.setMarca("Fiat");
        automovel.setModelo("Argo");
        automovel.setPlaca("ABC1D23");

    }

    @Test
    void naoCriaPedidoQuandoAutomovelEstaReservadoNoPeriodo() {
        LocalDate inicio = LocalDate.now().plusDays(5);
        LocalDate fim = inicio.plusDays(3);
        PedidoRequest dados = new PedidoRequest(automovel.getId(), inicio, fim, null);

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(pedidoRepository
                .existsByAutomovelIdAndStatusInAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                        eq(automovel.getId()), anyCollection(), eq(fim), eq(inicio)))
                .thenReturn(true);

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.criar(dados, cliente));

        assertTrue(erro.getMessage().contains("indisponível"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void modificarPedidoAprovadoExigeNovaAvaliacao() {
        PedidoAluguel pedido = pedido(StatusPedido.APROVADO);
        pedido.setParecer("Aprovado anteriormente");
        pedido.setAgenteAvaliador(agente);
        LocalDate novoInicio = LocalDate.now().plusDays(10);
        PedidoRequest dados = new PedidoRequest(automovel.getId(), novoInicio, novoInicio.plusDays(2), "Alterado");

        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        when(automovelService.buscarEntidade(automovel.getId())).thenReturn(automovel);
        when(pedidoRepository.save(any(PedidoAluguel.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        var resposta = service.modificar(pedido.getId(), dados, agente);

        assertEquals(StatusPedido.PENDENTE, resposta.status());
        assertNull(resposta.parecer());
        assertNull(resposta.nomeAgente());
    }

    @Test
    void naoAprovaPedidoQuandoAutomovelFicouIndisponivel() {
        PedidoAluguel pedido = pedido(StatusPedido.PENDENTE);
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        when(agenteRepository.findById(agente.getId())).thenReturn(Optional.of(agente));
        when(pedidoRepository
                .existsByAutomovelIdAndStatusInAndDataInicioLessThanEqualAndDataFimGreaterThanEqualAndIdNot(
                        eq(automovel.getId()), anyCollection(), eq(pedido.getDataFim()),
                        eq(pedido.getDataInicio()), eq(pedido.getId())))
                .thenReturn(true);

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.avaliar(pedido.getId(), new AvaliacaoRequest(true, "Parecer"), agente));

        assertTrue(erro.getMessage().contains("indisponível"));
        assertEquals(StatusPedido.PENDENTE, pedido.getStatus());
    }

    private PedidoAluguel pedido(StatusPedido status) {
        PedidoAluguel pedido = new PedidoAluguel();
        pedido.setId(20L);
        pedido.setCliente(cliente);
        pedido.setAutomovel(automovel);
        pedido.setDataInicio(LocalDate.now().plusDays(5));
        pedido.setDataFim(LocalDate.now().plusDays(8));
        pedido.setStatus(status);
        return pedido;
    }
}
