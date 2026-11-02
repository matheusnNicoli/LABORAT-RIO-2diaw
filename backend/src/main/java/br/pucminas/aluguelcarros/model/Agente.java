package br.pucminas.aluguelcarros.model;

import jakarta.persistence.*;

@Entity
@Table(name = "agentes")
public class Agente extends Usuario {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAgente tipo;

    public Agente() {
        setPerfil(Perfil.AGENTE);
    }

    @Override
    public String getNomeExibicao() {
        return nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public TipoAgente getTipo() {
        return tipo;
    }

    public void setTipo(TipoAgente tipo) {
        this.tipo = tipo;
    }
}
