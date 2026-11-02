package br.pucminas.aluguelcarros.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ClienteRequest(
        @NotBlank(message = "Informe o login") String login,
        String senha,
        @NotBlank(message = "Informe o RG") String rg,
        @NotBlank(message = "Informe o CPF") String cpf,
        @NotBlank(message = "Informe o nome") String nome,
        @NotBlank(message = "Informe o endereço") String endereco,
        String profissao,
        @Valid @Size(max = 3, message = "Máximo de 3 rendimentos") List<RendimentoDTO> rendimentos) {
}
