package br.pucminas.aluguelcarros.controller;

import br.pucminas.aluguelcarros.config.AuthInterceptor;
import br.pucminas.aluguelcarros.dto.AgenteResponse;
import br.pucminas.aluguelcarros.dto.ContratoResponse;
import br.pucminas.aluguelcarros.model.Usuario;
import br.pucminas.aluguelcarros.service.AgenteService;
import br.pucminas.aluguelcarros.service.ContratoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ContratoController {

    private final ContratoService contratoService;
    private final AgenteService agenteService;

    public ContratoController(ContratoService contratoService, AgenteService agenteService) {
        this.contratoService = contratoService;
        this.agenteService = agenteService;
    }

    @GetMapping("/api/contratos")
    public List<ContratoResponse> listar(@RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return contratoService.listar(logado);
    }

    @GetMapping("/api/agentes/bancos")
    public List<AgenteResponse> bancos() {
        return agenteService.listarBancos();
    }
}
