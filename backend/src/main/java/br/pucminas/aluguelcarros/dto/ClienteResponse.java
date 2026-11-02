package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.Cliente;

import java.util.List;

public record ClienteResponse(Long id, String login, String rg, String cpf, String nome,
                              String endereco, String profissao, List<RendimentoDTO> rendimentos) {

    public static ClienteResponse de(Cliente c) {
        List<RendimentoDTO> rends = c.getRendimentos().stream().map(RendimentoDTO::de).toList();
        return new ClienteResponse(c.getId(), c.getLogin(), c.getRg(), c.getCpf(), c.getNome(),
                c.getEndereco(), c.getProfissao(), rends);
    }
}
