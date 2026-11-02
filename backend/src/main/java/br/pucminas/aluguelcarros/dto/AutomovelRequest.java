package br.pucminas.aluguelcarros.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AutomovelRequest(
        @NotBlank(message = "Informe a matrícula") String matricula,
        @NotNull(message = "Informe o ano")
        @Min(value = 1950, message = "Ano inválido")
        @Max(value = 2100, message = "Ano inválido") Integer ano,
        @NotBlank(message = "Informe a marca") String marca,
        @NotBlank(message = "Informe o modelo") String modelo,
        @NotBlank(message = "Informe a placa") String placa) {
}
