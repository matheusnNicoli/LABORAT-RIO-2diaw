package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.Automovel;
import br.pucminas.aluguelcarros.model.TipoProprietario;

public record AutomovelResponse(Long id, String matricula, Integer ano, String marca, String modelo,
                                String placa, TipoProprietario tipoProprietario, String nomeProprietario) {

    public static AutomovelResponse de(Automovel a) {
        String nomeProp = a.getProprietario() != null ? a.getProprietario().getNomeExibicao() : null;
        return new AutomovelResponse(a.getId(), a.getMatricula(), a.getAno(), a.getMarca(), a.getModelo(),
                a.getPlaca(), a.getTipoProprietario(), nomeProp);
    }
}
