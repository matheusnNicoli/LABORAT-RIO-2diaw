package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.Agente;
import br.pucminas.aluguelcarros.model.TipoAgente;

public record AgenteResponse(Long id, String nome, String cnpj, TipoAgente tipo) {

    public static AgenteResponse de(Agente a) {
        return new AgenteResponse(a.getId(), a.getNome(), a.getCnpj(), a.getTipo());
    }
}
