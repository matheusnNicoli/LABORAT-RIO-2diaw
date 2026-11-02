package br.pucminas.aluguelcarros.config;

import br.pucminas.aluguelcarros.model.Usuario;
import br.pucminas.aluguelcarros.repository.UsuarioRepository;
import br.pucminas.aluguelcarros.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String USUARIO_LOGADO = "usuarioLogado";

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public AuthInterceptor(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if ("POST".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().equals("/api/clientes")) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return negar(response, "Faça login para acessar o sistema");
        }

        String token = header.substring(7);
        Long usuarioId = tokenService.buscarUsuarioId(token);
        if (usuarioId == null) {
            return negar(response, "Sessão expirada, faça login novamente");
        }

        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isEmpty()) {
            tokenService.remover(token);
            return negar(response, "Usuário não encontrado");
        }

        request.setAttribute(USUARIO_LOGADO, usuario.get());
        return true;
    }

    private boolean negar(HttpServletResponse response, String mensagem) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensagem\":\"" + mensagem + "\"}");
        return false;
    }
}
