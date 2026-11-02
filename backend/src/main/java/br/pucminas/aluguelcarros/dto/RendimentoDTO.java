package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.Rendimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RendimentoDTO(
        @NotBlank(message = "Informe a entidade empregadora") String entidadeEmpregadora,
        @NotNull(message = "Informe o valor do rendimento")
        @Positive(message = "O rendimento deve ser maior que zero") Double valor) {

    public static RendimentoDTO de(Rendimento r) {
        return new RendimentoDTO(r.getEntidadeEmpregadora(), r.getValor());
    }
}
