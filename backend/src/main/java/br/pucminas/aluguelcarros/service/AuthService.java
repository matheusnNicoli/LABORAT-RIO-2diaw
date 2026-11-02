package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.LoginRequest;
import br.pucminas.aluguelcarros.dto.LoginResponse;
import br.pucminas.aluguelcarros.exception.NaoAutorizadoException;
import br.pucminas.aluguelcarros.model.Agente;
import br.pucminas.aluguelcarros.model.Usuario;
import br.pucminas.aluguelcarros.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest dados) {
        Usuario usuario = usuarioRepository.findByLogin(dados.login().trim())
                .orElseThrow(() -> new NaoAutorizadoException("Login ou senha inválidos"));

        if (!passwordEncoder.matches(dados.senha(), usuario.getSenha())) {
            throw new NaoAutorizadoException("Login ou senha inválidos");
        }

        String token = tokenService.gerarToken(usuario.getId());
        return montarResposta(token, usuario);
    }

    public void logout(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            tokenService.remover(header.substring(7));
        }
    }

    public LoginResponse montarResposta(String token, Usuario usuario) {
        var tipo = usuario instanceof Agente a ? a.getTipo() : null;
        return new LoginResponse(token, usuario.getId(), usuario.getNomeExibicao(), usuario.getPerfil(), tipo);
    }
}
