package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.TipoProprietario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ContratoRequest(
        @NotNull(message = "Informe de quem fica a propriedade do carro") TipoProprietario tipoPropriedade,
        @NotNull(message = "Informe o valor do contrato")
        @Positive(message = "Valor deve ser maior que zero") Double valor,
        Long bancoId,
        Double valorCredito,
        Integer numeroParcelas,
        Double taxaJuros) {
}
