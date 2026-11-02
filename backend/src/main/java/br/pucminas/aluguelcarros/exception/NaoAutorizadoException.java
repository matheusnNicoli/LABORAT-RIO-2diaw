package br.pucminas.aluguelcarros.exception;

public class NaoAutorizadoException extends RuntimeException {
    public NaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}
