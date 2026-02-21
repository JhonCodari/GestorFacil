package com.JhonCodari.GestorFacil.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
import com.JhonCodari.GestorFacil.dto.TokenRespostaDTO;
import com.JhonCodari.GestorFacil.dto.RecuperacaoSenhaRequestDTO;
import com.JhonCodari.GestorFacil.dto.RedefinirSenhaRequestDTO;
import com.JhonCodari.GestorFacil.model.valueobjects.Token;
import com.JhonCodari.GestorFacil.model.valueobjects.RefreshToken;
import com.JhonCodari.GestorFacil.service.AutenticacaoService;
import com.JhonCodari.GestorFacil.service.AccessTokenService;
import com.JhonCodari.GestorFacil.service.RefreshTokenService;
import com.JhonCodari.GestorFacil.service.ConfirmacaoEmailService;
import com.JhonCodari.GestorFacil.service.RecuperacaoSenhaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
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
    public ResponseEntity<TokenRespostaDTO> login(@RequestBody @Valid UsuarioLoginDTO usuarioLoginDTO) {
        var accessToken = autenticacaoService.autenticar(usuarioLoginDTO);
        var refreshToken = refreshTokenService.criar(usuarioLoginDTO.email());
        
        var response = new TokenRespostaDTO(accessToken, refreshToken.valor());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") Token token) {
        var response = autenticacaoService.invalidarToken(token);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh-token")
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
    public ResponseEntity<String> confirmarEmail(@RequestParam String token) {
        confirmacaoEmailService.confirmarEmail(token);
        return ResponseEntity.ok("Email confirmado com sucesso!");
    }

    @PostMapping("/senha/esqueci")
    public ResponseEntity<String> solicitarRecuperacaoSenha(@RequestBody @Valid RecuperacaoSenhaRequestDTO request) {
        recuperacaoSenhaService.solicitarRecuperacao(request.getEmailUsuario());
        return ResponseEntity.ok("Email de recuperação enviado com sucesso!");
    }

    @PostMapping("/senha/reset")
    public ResponseEntity<String> redefinirSenha(
            @RequestParam String token,
            @RequestBody @Valid RedefinirSenhaRequestDTO request) {
        recuperacaoSenhaService.redefinirSenha(token, request.novaSenha());
        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
}

