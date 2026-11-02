package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.Perfil;
import br.pucminas.aluguelcarros.model.TipoAgente;

public record LoginResponse(String token, Long id, String nome, Perfil perfil, TipoAgente tipoAgente) {
}
