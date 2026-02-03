package com.JhonCodari.GestorFacil.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
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
    public ResponseEntity<String> login(@RequestBody @Valid UsuarioLoginDTO usuarioLoginDTO) {
        String token = autenticacaoService.autenticar(usuarioLoginDTO);
        return ResponseEntity.ok(token);
    }

    // @PostMapping("/refresh-token")// // Gera novo Access Token
    // public ResponseEntity<String> refreshToken(@RequestBody @Valid RefreshTokenDTO refreshTokenDTO) {
    //     return ResponseEntity.ok("Login realizado com sucesso!");
    // }
    
    // @PostMapping("/logout")// // Gera novo Access Token
    // public ResponseEntity<Void> logout() {
    //     // lógica de logout (ex: invalidar token, remover sessão, etc.)
    //     return ResponseEntity.ok().build();
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
