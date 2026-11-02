package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.ContratoRequest;
import br.pucminas.aluguelcarros.dto.ContratoResponse;
import br.pucminas.aluguelcarros.exception.AcessoNegadoException;
import br.pucminas.aluguelcarros.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguelcarros.exception.RegraNegocioException;
import br.pucminas.aluguelcarros.model.*;
import br.pucminas.aluguelcarros.repository.AgenteRepository;
import br.pucminas.aluguelcarros.repository.AutomovelRepository;
import br.pucminas.aluguelcarros.repository.ContratoRepository;
import br.pucminas.aluguelcarros.repository.PedidoAluguelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final PedidoAluguelRepository pedidoRepository;
    private final AgenteRepository agenteRepository;
    private final AutomovelRepository automovelRepository;
    private final PedidoService pedidoService;

    public ContratoService(ContratoRepository contratoRepository, PedidoAluguelRepository pedidoRepository,
                           AgenteRepository agenteRepository, AutomovelRepository automovelRepository,
                           PedidoService pedidoService) {
        this.contratoRepository = contratoRepository;
        this.pedidoRepository = pedidoRepository;
        this.agenteRepository = agenteRepository;
        this.automovelRepository = automovelRepository;
        this.pedidoService = pedidoService;
    }

    @Transactional(readOnly = true)
    public List<ContratoResponse> listar(Usuario logado) {
        List<Contrato> contratos;
        if (Permissoes.ehAgente(logado)) {
            contratos = contratoRepository.findAllByOrderByIdDesc();
        } else {
            contratos = contratoRepository.findByPedidoClienteIdOrderByIdDesc(logado.getId());
        }
        return contratos.stream().map(ContratoResponse::de).toList();
    }

    @Transactional
    public ContratoResponse executar(Long pedidoId, ContratoRequest dados, Usuario logado) {
        Permissoes.exigirAgente(logado);
        Agente agente = agenteRepository.findById(logado.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agente não encontrado"));
        PedidoAluguel pedido = pedidoService.buscarEntidade(pedidoId);

        if (pedido.getStatus() != StatusPedido.APROVADO) {
            throw new RegraNegocioException("Só é possível gerar contrato de pedidos aprovados");
        }
        if (contratoRepository.existsByPedidoId(pedidoId)) {
            throw new RegraNegocioException("Esse pedido já tem contrato");
        }

        Contrato contrato = new Contrato();
        contrato.setPedido(pedido);
        contrato.setAgente(agente);
        contrato.setDataContrato(LocalDate.now());
        contrato.setTipoPropriedade(dados.tipoPropriedade());
        contrato.setValor(dados.valor());

        ContratoCredito credito = null;
        if (dados.bancoId() != null) {
            if (agente.getTipo() != TipoAgente.BANCO) {
                throw new AcessoNegadoException("Somente um banco pode conceder contrato de crédito");
            }
            Agente banco = agenteRepository.findById(dados.bancoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Banco não encontrado"));
            if (banco.getTipo() != TipoAgente.BANCO) {
                throw new RegraNegocioException("O crédito só pode ser concedido por um banco");
            }
            if (!banco.getId().equals(agente.getId())) {
                throw new AcessoNegadoException("O banco do crédito deve ser o banco autenticado");
            }
            if (dados.valorCredito() == null || dados.valorCredito() <= 0) {
                throw new RegraNegocioException("Informe o valor do crédito");
            }
            credito = new ContratoCredito();
            credito.setBanco(banco);
            credito.setValor(dados.valorCredito());
            credito.setNumeroParcelas(dados.numeroParcelas());
            credito.setTaxaJuros(dados.taxaJuros());
            contrato.setContratoCredito(credito);
        }

        Automovel carro = pedido.getAutomovel();
        carro.setTipoProprietario(dados.tipoPropriedade());
        carro.setProprietario(definirProprietario(dados.tipoPropriedade(), pedido, agente, credito));
        automovelRepository.save(carro);

        pedido.setStatus(StatusPedido.CONTRATADO);
        pedidoRepository.save(pedido);

        return ContratoResponse.de(contratoRepository.save(contrato));
    }

    private Usuario definirProprietario(TipoProprietario tipo, PedidoAluguel pedido, Agente agente, ContratoCredito credito) {
        switch (tipo) {
            case CLIENTE:
                return pedido.getCliente();
            case EMPRESA:
                if (agente.getTipo() != TipoAgente.EMPRESA) {
                    throw new RegraNegocioException("Para registrar no nome da empresa, o contrato deve ser feito por uma empresa");
                }
                return agente;
            case BANCO:
                if (credito != null) {
                    return credito.getBanco();
                }
                if (agente.getTipo() == TipoAgente.BANCO) {
                    return agente;
                }
                throw new RegraNegocioException("Para registrar no nome do banco, informe o banco do crédito");
            default:
                throw new RegraNegocioException("Tipo de propriedade inválido");
        }
    }
}
