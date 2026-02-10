package com.JhonCodari.GestorFacil.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
import com.JhonCodari.GestorFacil.dto.RefreshTokenRequestDTO;
import com.JhonCodari.GestorFacil.dto.TokenRespostaDTO;
import com.JhonCodari.GestorFacil.model.valueobjects.Token;
import com.JhonCodari.GestorFacil.service.AutenticacaoService;
import com.JhonCodari.GestorFacil.service.AccessTokenService;
import com.JhonCodari.GestorFacil.service.RefreshTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {    

    private final AutenticacaoService autenticacaoService;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    public AutenticacaoController(
            AutenticacaoService autenticacaoService,
            AccessTokenService accessTokenService,
            RefreshTokenService refreshTokenService) {
        this.autenticacaoService = autenticacaoService;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
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
    public ResponseEntity<TokenRespostaDTO> refreshToken(@RequestBody @Valid RefreshTokenRequestDTO refreshTokenRequestDTO) {
        var novoAccessToken = accessTokenService.renovar(refreshTokenRequestDTO.refreshToken());
        
        var response = new TokenRespostaDTO(
            novoAccessToken.valor(),
            refreshTokenRequestDTO.refreshToken().valor()
        );
        
        return ResponseEntity.ok(response);
    }

    // @PostMapping("/senha/recuperar")
    // public ResponseEntity<Void> solicitarRecuperacaoSenha(@RequestBody @Valid RecuperacaoSenhaDTO recuperacaoSenhaDTO) {
    //     return null;
    // }

    // @PostMapping("/senha/alterar")
    // public ResponseEntity<Void> alterarSenha(@RequestBody @Valid AlterarSenhaDTO alterarSenhaDTO) {
    //     return null;
    // }
}

