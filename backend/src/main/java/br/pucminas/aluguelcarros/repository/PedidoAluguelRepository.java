package br.pucminas.aluguelcarros.repository;

import br.pucminas.aluguelcarros.model.PedidoAluguel;
import br.pucminas.aluguelcarros.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface PedidoAluguelRepository extends JpaRepository<PedidoAluguel, Long> {

    List<PedidoAluguel> findByClienteIdOrderByDataCriacaoDesc(Long clienteId);

    List<PedidoAluguel> findAllByOrderByDataCriacaoDesc();

    boolean existsByClienteId(Long clienteId);

    boolean existsByAutomovelId(Long automovelId);

    boolean existsByAutomovelIdAndStatusInAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
            Long automovelId, Collection<StatusPedido> status, LocalDate dataFim, LocalDate dataInicio);

    boolean existsByAutomovelIdAndStatusInAndDataInicioLessThanEqualAndDataFimGreaterThanEqualAndIdNot(
            Long automovelId, Collection<StatusPedido> status, LocalDate dataFim, LocalDate dataInicio,
            Long pedidoIgnoradoId);
}
