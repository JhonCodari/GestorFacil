package com.JhonCodari.GestorFacil.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
import com.JhonCodari.GestorFacil.model.valueobjects.Token;
import com.JhonCodari.GestorFacil.service.AutenticacaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {    

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid UsuarioLoginDTO usuarioLoginDTO) {
        Token token = new Token(autenticacaoService.autenticar(usuarioLoginDTO));
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .header("authorization", token.comPrefixoBearer())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") Token token) {
        var response = autenticacaoService.invalidarToken(token);
        return ResponseEntity.ok().body(response);
    }


    
    // @PostMapping("/refresh-token")// // Gera novo Access Token
    // public ResponseEntity<String> refreshToken(@RequestBody @Valid RefreshTokenDTO refreshTokenDTO) {
    //     return ResponseEntity.ok("Login realizado com sucesso!");
    // }    

    // @PostMapping("/senha/recuperar") // Inicia fluxo de recuperação
    // public ResponseEntity<Void> solicitarRecuperacaoSenha(@RequestBody @Valid RecuperacaoSenhaDTO recuperacaoSenhaDTO) {
    //     //TODO: process POST request
        
    //     return null;
    // }

    // @PostMapping("/senha/alterar") // Finaliza recuperação com token
    // public ResponseEntity<Void> alterarSenha(@RequestBody @Valid AlterarSenhaDTO alterarSenhaDTO) {
    //     //TODO: process POST request
        
    //     return null;
    // }
}
