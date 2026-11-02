package br.pucminas.aluguelcarros.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private final Map<String, Long> tokens = new ConcurrentHashMap<>();

    public String gerarToken(Long usuarioId) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, usuarioId);
        return token;
    }

    public Long buscarUsuarioId(String token) {
        return tokens.get(token);
    }

    public void remover(String token) {
        tokens.remove(token);
    }
}
