package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.exception.AcessoNegadoException;
import br.pucminas.aluguelcarros.model.Agente;
import br.pucminas.aluguelcarros.model.Cliente;
import br.pucminas.aluguelcarros.model.Usuario;

public class Permissoes {

    private Permissoes() {
    }

    public static Agente exigirAgente(Usuario usuario) {
        if (usuario instanceof Agente agente) {
            return agente;
        }
        throw new AcessoNegadoException("Apenas agentes (empresas e bancos) podem fazer isso");
    }

    public static Cliente exigirCliente(Usuario usuario) {
        if (usuario instanceof Cliente cliente) {
            return cliente;
        }
        throw new AcessoNegadoException("Apenas clientes podem fazer isso");
    }

    public static boolean ehAgente(Usuario usuario) {
        return usuario instanceof Agente;
    }
}
