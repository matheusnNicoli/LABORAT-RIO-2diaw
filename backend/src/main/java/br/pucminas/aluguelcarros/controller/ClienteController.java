package br.pucminas.aluguelcarros.controller;

import br.pucminas.aluguelcarros.config.AuthInterceptor;
import br.pucminas.aluguelcarros.dto.ClienteRequest;
import br.pucminas.aluguelcarros.dto.ClienteResponse;
import br.pucminas.aluguelcarros.model.Usuario;
import br.pucminas.aluguelcarros.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(@RequestBody @Valid ClienteRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.cadastrar(dados));
    }

    @GetMapping
    public List<ClienteResponse> listar(@RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return clienteService.listar(logado);
    }

    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Long id, @RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return clienteService.buscar(id, logado);
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable Long id, @RequestBody @Valid ClienteRequest dados,
                                     @RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        return clienteService.atualizar(id, dados, logado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, @RequestAttribute(AuthInterceptor.USUARIO_LOGADO) Usuario logado) {
        clienteService.excluir(id, logado);
        return ResponseEntity.noContent().build();
    }
}
