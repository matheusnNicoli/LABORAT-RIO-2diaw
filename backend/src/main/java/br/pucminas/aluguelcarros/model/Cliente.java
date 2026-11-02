package br.pucminas.aluguelcarros.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente extends Usuario {

    @Column(nullable = false)
    private String rg;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String endereco;

    private String profissao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private List<Rendimento> rendimentos = new ArrayList<>();

    public Cliente() {
        setPerfil(Perfil.CLIENTE);
    }

    @Override
    public String getNomeExibicao() {
        return nome;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public List<Rendimento> getRendimentos() {
        return rendimentos;
    }

    public void setRendimentos(List<Rendimento> rendimentos) {
        this.rendimentos = rendimentos;
    }
}
