package com.JhonCodari.GestorFacil.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> tratarRequisicaoJsonInvalida(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requisição inválida: " + ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> tratarViolacaoIntegridadeDados(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("dataIntegrityViolation", "Violação de integridade de dados: " + ex.getMostSpecificCause().getMessage());        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(LimiteTentativasExcedidoException.class)
    public ResponseEntity<String> tratarLimiteTentativasExcedidoException(LimiteTentativasExcedidoException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Limite de tentativas excedido: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarExcecoesValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<String> tratarEmailJaCadastradoException(EmailJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Falha ao cadastrar e-mail: " + ex.getMessage());
    }

    @ExceptionHandler(ConfirmacaoEmailException.class)
    public ResponseEntity<String> tratarConfirmacaoEmailException(ConfirmacaoEmailException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro na confirmação de e-mail: " + ex.getMessage());
    }

    @ExceptionHandler(ServicoEmailIndisponivelException.class)
    public ResponseEntity<String> tratarServicoEmailIndisponivelException(ServicoEmailIndisponivelException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Serviço de e-mail indisponível: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> tratarExcecaoGenerica(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado.");
    }
    
}
