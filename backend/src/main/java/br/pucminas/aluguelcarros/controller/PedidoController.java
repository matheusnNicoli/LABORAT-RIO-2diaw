package br.pucminas.aluguelcarros.controller;

import br.pucminas.aluguelcarros.config.AuthInterceptor;
import br.pucminas.aluguelcarros.dto.*;
import br.pucminas.aluguelcarros.model.Usuario;
import br.pucminas.aluguelcarros.service.ContratoService;
import br.pucminas.aluguelcarros.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ContratoService contratoService;

    public PedidoController(PedidoService pedidoService, ContratoService contratoService) {
        this.pedidoService = pedidoService;
        this.contratoService = contratoService;
    }

    @GetMapping
    public List<PedidoResponse> listar(@RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return pedidoService.listar(logado);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id, @RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return pedidoService.buscar(id, logado);
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@RequestBody @Valid PedidoRequest dados,
                                                @RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criar(dados, logado));
    }

    @PutMapping("/{id}")
    public PedidoResponse modificar(@PathVariable Long id, @RequestBody @Valid PedidoRequest dados,
                                    @RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return pedidoService.modificar(id, dados, logado);
    }

    @PatchMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id, @RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return pedidoService.cancelar(id, logado);
    }

    @PatchMapping("/{id}/avaliar")
    public PedidoResponse avaliar(@PathVariable Long id, @RequestBody @Valid AvaliacaoRequest dados,
                                  @RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return pedidoService.avaliar(id, dados, logado);
    }

    @PostMapping("/{id}/contrato")
    public ResponseEntity<ContratoResponse> gerarContrato(@PathVariable Long id, @RequestBody @Valid ContratoRequest dados,
                                                          @RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratoService.executar(id, dados, logado));
    }
}
