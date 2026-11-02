package br.pucminas.aluguelcarros.repository;

import br.pucminas.aluguelcarros.model.Agente;
import br.pucminas.aluguelcarros.model.TipoAgente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgenteRepository extends JpaRepository<Agente, Long> {

    List<Agente> findByTipo(TipoAgente tipo);
}
