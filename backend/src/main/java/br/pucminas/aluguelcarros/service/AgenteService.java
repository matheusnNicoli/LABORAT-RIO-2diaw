package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.AgenteResponse;
import br.pucminas.aluguelcarros.model.TipoAgente;
import br.pucminas.aluguelcarros.repository.AgenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgenteService {

    private final AgenteRepository agenteRepository;

    public AgenteService(AgenteRepository agenteRepository) {
        this.agenteRepository = agenteRepository;
    }

    public List<AgenteResponse> listarBancos() {
        return agenteRepository.findByTipo(TipoAgente.BANCO).stream().map(AgenteResponse::de).toList();
    }
}
