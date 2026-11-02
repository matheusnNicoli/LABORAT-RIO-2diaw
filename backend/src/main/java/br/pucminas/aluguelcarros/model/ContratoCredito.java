package br.pucminas.aluguelcarros.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contratos_credito")
public class ContratoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "banco_id")
    private Agente banco;

    @Column(nullable = false)
    private Double valor;

    private Integer numeroParcelas;

    private Double taxaJuros;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Agente getBanco() {
        return banco;
    }

    public void setBanco(Agente banco) {
        this.banco = banco;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Integer getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(Integer numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public Double getTaxaJuros() {
        return taxaJuros;
    }

    public void setTaxaJuros(Double taxaJuros) {
        this.taxaJuros = taxaJuros;
    }
}
