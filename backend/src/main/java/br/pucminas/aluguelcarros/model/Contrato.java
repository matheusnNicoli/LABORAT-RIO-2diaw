package br.pucminas.aluguelcarros.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "pedido_id", unique = true)
    private PedidoAluguel pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agente_id")
    private Agente agente;

    private LocalDate dataContrato;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProprietario tipoPropriedade;

    @Column(nullable = false)
    private Double valor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contrato_credito_id")
    private ContratoCredito contratoCredito;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PedidoAluguel getPedido() {
        return pedido;
    }

    public void setPedido(PedidoAluguel pedido) {
        this.pedido = pedido;
    }

    public Agente getAgente() {
        return agente;
    }

    public void setAgente(Agente agente) {
        this.agente = agente;
    }

    public LocalDate getDataContrato() {
        return dataContrato;
    }

    public void setDataContrato(LocalDate dataContrato) {
        this.dataContrato = dataContrato;
    }

    public TipoProprietario getTipoPropriedade() {
        return tipoPropriedade;
    }

    public void setTipoPropriedade(TipoProprietario tipoPropriedade) {
        this.tipoPropriedade = tipoPropriedade;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public ContratoCredito getContratoCredito() {
        return contratoCredito;
    }

    public void setContratoCredito(ContratoCredito contratoCredito) {
        this.contratoCredito = contratoCredito;
    }
}
