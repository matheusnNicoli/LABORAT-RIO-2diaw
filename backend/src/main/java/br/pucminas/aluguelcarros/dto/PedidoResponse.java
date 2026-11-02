package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.PedidoAluguel;
import br.pucminas.aluguelcarros.model.StatusPedido;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PedidoResponse(Long id, Long clienteId, String nomeCliente, AutomovelResponse automovel,
                             LocalDate dataInicio, LocalDate dataFim, LocalDateTime dataCriacao,
                             StatusPedido status, String observacao, String parecer, String nomeAgente) {

    public static PedidoResponse de(PedidoAluguel p) {
        String agente = p.getAgenteAvaliador() != null ? p.getAgenteAvaliador().getNome() : null;
        return new PedidoResponse(p.getId(), p.getCliente().getId(), p.getCliente().getNome(),
                AutomovelResponse.de(p.getAutomovel()), p.getDataInicio(), p.getDataFim(),
                p.getDataCriacao(), p.getStatus(), p.getObservacao(), p.getParecer(), agente);
    }
}
