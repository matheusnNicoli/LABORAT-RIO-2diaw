package br.pucminas.aluguelcarros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AvaliacaoRequest(
        @NotNull(message = "Informe se o pedido foi aprovado") Boolean aprovado,
        @NotBlank(message = "Escreva o parecer") String parecer) {
}
