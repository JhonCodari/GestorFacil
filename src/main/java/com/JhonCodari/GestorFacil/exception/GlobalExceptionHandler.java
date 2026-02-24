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

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<String> tratarCredenciaisInvalidasException(CredenciaisInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas: " + ex.getMessage());
    }

    @ExceptionHandler(ConfirmacaoEmailException.class)
    public ResponseEntity<String> tratarConfirmacaoEmailException(ConfirmacaoEmailException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro na confirmação de e-mail: " + ex.getMessage());
    }

    @ExceptionHandler(ServicoEmailIndisponivelException.class)
    public ResponseEntity<String> tratarServicoEmailIndisponivelException(ServicoEmailIndisponivelException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Serviço de e-mail indisponível: " + ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenExpiradoException.class)
    public ResponseEntity<String> tratarRefreshTokenExpiradoException(RefreshTokenExpiradoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expirado: " + ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenRevogadoException.class)
    public ResponseEntity<String> tratarRefreshTokenRevogadoException(RefreshTokenRevogadoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token revogado: " + ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenNaoEncontradoException.class)
    public ResponseEntity<String> tratarRefreshTokenNaoEncontradoException(RefreshTokenNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Refresh token não encontrado: " + ex.getMessage());
    }

    @ExceptionHandler(TokenNaBlacklistException.class)
    public ResponseEntity<String> tratarTokenNaBlacklistException(TokenNaBlacklistException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido: " + ex.getMessage());
    }

    @ExceptionHandler(EmailNaoVerificadoException.class)
    public ResponseEntity<String> tratarEmailNaoVerificadoException(EmailNaoVerificadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email não verificado: " + ex.getMessage());
    }

    @ExceptionHandler(TransacaoNaoEncontradaException.class)
    public ResponseEntity<String> tratarTransacaoNaoEncontradaException(TransacaoNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(TransacaoNaoPertenceAoUsuarioException.class)
    public ResponseEntity<String> tratarTransacaoNaoPertenceAoUsuarioException(TransacaoNaoPertenceAoUsuarioException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> tratarExcecaoGenerica(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
