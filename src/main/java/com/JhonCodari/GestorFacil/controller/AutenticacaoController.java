package com.JhonCodari.GestorFacil.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.JhonCodari.GestorFacil.dto.*;
import com.JhonCodari.GestorFacil.model.valueobjects.Token;
import com.JhonCodari.GestorFacil.model.valueobjects.RefreshToken;
import com.JhonCodari.GestorFacil.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticacao", description = "Login, logout, refresh token, confirmacao de email e recuperacao de senha")
public class AutenticacaoController {    

    private final AutenticacaoService autenticacaoService;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final ConfirmacaoEmailService confirmacaoEmailService;
    private final RecuperacaoSenhaService recuperacaoSenhaService;

    public AutenticacaoController(
            AutenticacaoService autenticacaoService,
            AccessTokenService accessTokenService,
            RefreshTokenService refreshTokenService,
            ConfirmacaoEmailService confirmacaoEmailService,
            RecuperacaoSenhaService recuperacaoSenhaService) {
        this.autenticacaoService = autenticacaoService;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
        this.confirmacaoEmailService = confirmacaoEmailService;
        this.recuperacaoSenhaService = recuperacaoSenhaService;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario e obter tokens JWT")
    public ResponseEntity<TokenRespostaDTO> login(@RequestBody @Valid UsuarioLoginDTO usuarioLoginDTO) {
        var accessToken = autenticacaoService.autenticar(usuarioLoginDTO);
        var refreshToken = refreshTokenService.criar(usuarioLoginDTO.email());
        
        var response = new TokenRespostaDTO(accessToken, refreshToken.valor());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalidar tokens e realizar logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") Token token) {
        var response = autenticacaoService.invalidarToken(token);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh-token")
    @Operation(summary = "Renovar access token usando refresh token")
    public ResponseEntity<TokenRespostaDTO> refreshToken(@RequestHeader("X-Refresh-Token") String refreshTokenValor) {
        var refreshToken = new RefreshToken(refreshTokenValor);
        
        var novoRefreshToken = refreshTokenService.rotacionar(refreshToken);
        var novoAccessToken = accessTokenService.renovar(novoRefreshToken);
        
        var response = new TokenRespostaDTO(
            novoAccessToken.valor(),
            novoRefreshToken.valor()
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirma-email")
    @Operation(summary = "Confirmar email do usuario via token")
    public ResponseEntity<String> confirmarEmail(@RequestParam String token) {
        confirmacaoEmailService.confirmarEmail(token);
        return ResponseEntity.ok("Email confirmado com sucesso!");
    }

    @PostMapping("/senha/esqueci")
    @Operation(summary = "Solicitar recuperacao de senha por email")
    public ResponseEntity<String> solicitarRecuperacaoSenha(@RequestBody @Valid RecuperacaoSenhaRequestDTO request) {
        recuperacaoSenhaService.solicitarRecuperacao(request.getEmailUsuario());
        return ResponseEntity.ok("Email de recuperação enviado com sucesso!");
    }

    @PostMapping("/senha/reset")
    @Operation(summary = "Redefinir senha usando token de recuperacao")
    public ResponseEntity<String> redefinirSenha(
            @RequestParam String token,
            @RequestBody @Valid RedefinirSenhaRequestDTO request) {
        recuperacaoSenhaService.redefinirSenha(token, request.novaSenha());
        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
}

