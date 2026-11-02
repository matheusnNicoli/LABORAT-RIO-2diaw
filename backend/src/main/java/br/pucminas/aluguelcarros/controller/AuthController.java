package br.pucminas.aluguelcarros.controller;

import br.pucminas.aluguelcarros.config.AuthInterceptor;
import br.pucminas.aluguelcarros.dto.LoginRequest;
import br.pucminas.aluguelcarros.dto.LoginResponse;
import br.pucminas.aluguelcarros.model.Usuario;
import br.pucminas.aluguelcarros.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest dados) {
        return authService.login(dados);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String header) {
        authService.logout(header);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public LoginResponse me(@RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado,
                            @RequestHeader("Authorization") String header) {
        return authService.montarResposta(header.substring(7), logado);
    }
}
