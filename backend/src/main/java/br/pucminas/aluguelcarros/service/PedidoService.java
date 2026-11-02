package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.AvaliacaoRequest;
import br.pucminas.aluguelcarros.dto.PedidoRequest;
import br.pucminas.aluguelcarros.dto.PedidoResponse;
import br.pucminas.aluguelcarros.exception.AcessoNegadoException;
import br.pucminas.aluguelcarros.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguelcarros.exception.RegraNegocioException;
import br.pucminas.aluguelcarros.model.*;
import br.pucminas.aluguelcarros.repository.AgenteRepository;
import br.pucminas.aluguelcarros.repository.ClienteRepository;
import br.pucminas.aluguelcarros.repository.PedidoAluguelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
public class PedidoService {

    private static final EnumSet<StatusPedido> STATUS_QUE_RESERVAM_AUTOMOVEL =
            EnumSet.of(StatusPedido.APROVADO, StatusPedido.CONTRATADO);

    private final PedidoAluguelRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final AgenteRepository agenteRepository;
    private final AutomovelService automovelService;

    public PedidoService(PedidoAluguelRepository pedidoRepository, ClienteRepository clienteRepository,
                         AgenteRepository agenteRepository, AutomovelService automovelService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.agenteRepository = agenteRepository;
        this.automovelService = automovelService;
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(Usuario logado) {
        List<PedidoAluguel> pedidos;
        if (Permissoes.ehAgente(logado)) {
            pedidos = pedidoRepository.findAllByOrderByDataCriacaoDesc();
        } else {
            pedidos = pedidoRepository.findByClienteIdOrderByDataCriacaoDesc(logado.getId());
        }
        return pedidos.stream().map(PedidoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(Long id, Usuario logado) {
        return PedidoResponse.de(buscarComAcesso(id, logado));
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest dados, Usuario logado) {
        Permissoes.exigirCliente(logado);
        Cliente cliente = clienteRepository.findById(logado.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
        validarDatas(dados);
        validarDisponibilidade(dados.automovelId(), dados.dataInicio(), dados.dataFim(), null);

        PedidoAluguel pedido = new PedidoAluguel();
        pedido.setCliente(cliente);
        pedido.setAutomovel(automovelService.buscarEntidade(dados.automovelId()));
        pedido.setDataInicio(dados.dataInicio());
        pedido.setDataFim(dados.dataFim());
        pedido.setObservacao(dados.observacao());
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse modificar(Long id, PedidoRequest dados, Usuario logado) {
        PedidoAluguel pedido = buscarComAcesso(id, logado);
        boolean estavaAprovado = pedido.getStatus() == StatusPedido.APROVADO;

        if (Permissoes.ehAgente(logado)) {
            if (pedido.getStatus() != StatusPedido.PENDENTE && pedido.getStatus() != StatusPedido.APROVADO) {
                throw new RegraNegocioException("Esse pedido não pode mais ser modificado");
            }
        } else if (!pedido.podeSerAlteradoPeloCliente()) {
            throw new RegraNegocioException("Só é possível modificar pedidos pendentes");
        }

        validarDatas(dados);
        validarDisponibilidade(dados.automovelId(), dados.dataInicio(), dados.dataFim(), pedido.getId());
        pedido.setAutomovel(automovelService.buscarEntidade(dados.automovelId()));
        pedido.setDataInicio(dados.dataInicio());
        pedido.setDataFim(dados.dataFim());
        pedido.setObservacao(dados.observacao());
        if (estavaAprovado) {
            pedido.setStatus(StatusPedido.PENDENTE);
            pedido.setParecer(null);
            pedido.setAgenteAvaliador(null);
        }
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse cancelar(Long id, Usuario logado) {
        Permissoes.exigirCliente(logado);
        PedidoAluguel pedido = buscarComAcesso(id, logado);
        if (!pedido.podeSerAlteradoPeloCliente()) {
            throw new RegraNegocioException("Só é possível cancelar pedidos pendentes");
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse avaliar(Long id, AvaliacaoRequest dados, Usuario logado) {
        Permissoes.exigirAgente(logado);
        Agente agente = agenteRepository.findById(logado.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agente não encontrado"));
        PedidoAluguel pedido = buscarEntidade(id);
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new RegraNegocioException("Só é possível avaliar pedidos pendentes");
        }
        if (dados.aprovado()) {
            validarDisponibilidade(pedido.getAutomovel().getId(), pedido.getDataInicio(), pedido.getDataFim(), pedido.getId());
        }
        pedido.setStatus(dados.aprovado() ? StatusPedido.APROVADO : StatusPedido.REPROVADO);
        pedido.setParecer(dados.parecer());
        pedido.setAgenteAvaliador(agente);
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    public PedidoAluguel buscarEntidade(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));
    }

    private PedidoAluguel buscarComAcesso(Long id, Usuario logado) {
        PedidoAluguel pedido = buscarEntidade(id);
        if (!Permissoes.ehAgente(logado) && !pedido.getCliente().getId().equals(logado.getId())) {
            throw new AcessoNegadoException("Esse pedido não é seu");
        }
        return pedido;
    }

    private void validarDatas(PedidoRequest dados) {
        if (dados.dataInicio().isBefore(LocalDate.now())) {
            throw new RegraNegocioException("A data de início não pode ser no passado");
        }
        if (!dados.dataFim().isAfter(dados.dataInicio())) {
            throw new RegraNegocioException("A data de fim deve ser depois da data de início");
        }
    }

    private void validarDisponibilidade(Long automovelId, LocalDate dataInicio, LocalDate dataFim,
                                        Long pedidoIgnoradoId) {
        boolean conflito;
        if (pedidoIgnoradoId == null) {
            conflito = pedidoRepository
                    .existsByAutomovelIdAndStatusInAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                            automovelId, STATUS_QUE_RESERVAM_AUTOMOVEL, dataFim, dataInicio);
        } else {
            conflito = pedidoRepository
                    .existsByAutomovelIdAndStatusInAndDataInicioLessThanEqualAndDataFimGreaterThanEqualAndIdNot(
                            automovelId, STATUS_QUE_RESERVAM_AUTOMOVEL, dataFim, dataInicio, pedidoIgnoradoId);
        }
        if (conflito) {
            throw new RegraNegocioException("Automóvel indisponível no período informado");
        }
    }
}
