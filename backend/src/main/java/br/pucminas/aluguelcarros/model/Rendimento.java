package br.pucminas.aluguelcarros.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rendimentos")
public class Rendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entidadeEmpregadora;

    @Column(nullable = false)
    private Double valor;

    public Rendimento() {
    }

    public Rendimento(String entidadeEmpregadora, Double valor) {
        this.entidadeEmpregadora = entidadeEmpregadora;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntidadeEmpregadora() {
        return entidadeEmpregadora;
    }

    public void setEntidadeEmpregadora(String entidadeEmpregadora) {
        this.entidadeEmpregadora = entidadeEmpregadora;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
