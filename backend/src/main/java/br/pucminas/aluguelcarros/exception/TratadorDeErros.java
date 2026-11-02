package br.pucminas.aluguelcarros.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> regraNegocio(RegraNegocioException e) {
        return erro(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(RecursoNaoEncontradoException e) {
        return erro(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Map<String, String>> acessoNegado(AcessoNegadoException e) {
        return erro(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(NaoAutorizadoException.class)
    public ResponseEntity<Map<String, String>> naoAutorizado(NaoAutorizadoException e) {
        return erro(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException e) {
        String msg = "Dados inválidos";
        if (e.getBindingResult().getFieldError() != null) {
            msg = e.getBindingResult().getFieldError().getDefaultMessage();
        }
        return erro(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException e) {
        return erro(HttpStatus.BAD_REQUEST, "Formato dos dados inválido");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> integridade(DataIntegrityViolationException e) {
        return erro(HttpStatus.BAD_REQUEST, "Operação não permitida: dado duplicado ou em uso");
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("mensagem", mensagem));
    }
}
