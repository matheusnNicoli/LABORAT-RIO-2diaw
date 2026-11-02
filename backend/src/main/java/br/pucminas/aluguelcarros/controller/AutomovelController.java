package br.pucminas.aluguelcarros.controller;

import br.pucminas.aluguelcarros.config.AuthInterceptor;
import br.pucminas.aluguelcarros.dto.AutomovelRequest;
import br.pucminas.aluguelcarros.dto.AutomovelResponse;
import br.pucminas.aluguelcarros.model.Usuario;
import br.pucminas.aluguelcarros.service.AutomovelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/automoveis")
public class AutomovelController {

    private final AutomovelService automovelService;

    public AutomovelController(AutomovelService automovelService) {
        this.automovelService = automovelService;
    }

    @GetMapping
    public List<AutomovelResponse> listar() {
        return automovelService.listar();
    }

    @GetMapping("/{id}")
    public AutomovelResponse buscar(@PathVariable Long id) {
        return automovelService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<AutomovelResponse> cadastrar(@RequestBody @Valid AutomovelRequest dados,
                                                       @RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(automovelService.cadastrar(dados, logado));
    }

    @PutMapping("/{id}")
    public AutomovelResponse atualizar(@PathVariable Long id, @RequestBody @Valid AutomovelRequest dados,
                                       @RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return automovelService.atualizar(id, dados, logado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, @RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        automovelService.excluir(id, logado);
        return ResponseEntity.noContent().build();
    }
}
