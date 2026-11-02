package br.pucminas.aluguelcarros.repository;

import br.pucminas.aluguelcarros.model.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByPedidoClienteIdOrderByIdDesc(Long clienteId);

    List<Contrato> findAllByOrderByIdDesc();

    boolean existsByPedidoId(Long pedidoId);
}
