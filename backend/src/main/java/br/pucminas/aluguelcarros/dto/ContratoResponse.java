package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.Contrato;
import br.pucminas.aluguelcarros.model.ContratoCredito;
import br.pucminas.aluguelcarros.model.TipoProprietario;

import java.time.LocalDate;

public record ContratoResponse(Long id, Long pedidoId, String nomeCliente, String automovel,
                               LocalDate dataContrato, TipoProprietario tipoPropriedade, Double valor,
                               String nomeAgente, String nomeBanco, Double valorCredito,
                               Integer numeroParcelas, Double taxaJuros) {

    public static ContratoResponse de(Contrato c) {
        var carro = c.getPedido().getAutomovel();
        String descCarro = carro.getMarca() + " " + carro.getModelo() + " (" + carro.getPlaca() + ")";
        ContratoCredito cred = c.getContratoCredito();
        return new ContratoResponse(c.getId(), c.getPedido().getId(), c.getPedido().getCliente().getNome(),
                descCarro, c.getDataContrato(), c.getTipoPropriedade(), c.getValor(), c.getAgente().getNome(),
                cred != null ? cred.getBanco().getNome() : null,
                cred != null ? cred.getValor() : null,
                cred != null ? cred.getNumeroParcelas() : null,
                cred != null ? cred.getTaxaJuros() : null);
    }
}
