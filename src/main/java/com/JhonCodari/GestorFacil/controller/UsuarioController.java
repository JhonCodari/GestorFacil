package com.JhonCodari.GestorFacil.controller;

import org.springframework.web.bind.annotation.RestController;

import com.JhonCodari.GestorFacil.dto.UsuarioCadastroDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;

import jakarta.validation.Valid;
import com.JhonCodari.GestorFacil.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioRespostaDTO> cadastro(@RequestBody @Valid UsuarioCadastroDTO usuarioCadastroDTO) {
        UsuarioRespostaDTO resposta = this.usuarioService.cadastrarUsuario(usuarioCadastroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    // @PostMapping("/login")
    // public ResponseEntity<String> login(@RequestBody @Valid UsuarioLoginDTO usuarioLoginDTO) {
    //     return ResponseEntity.ok("Login realizado com sucesso!");
    // }

    // @PostMapping("/recuperar-senha")
    // public ResponseEntity<Void> solicitarRecuperacaoSenha(@RequestBody @Valid RecuperacaoSenhaDTO recuperacaoSenhaDTO) {
    //     //TODO: process POST request
        
    //     return null;
    // }

    // @PostMapping("/alterar-senha")
    // public ResponseEntity<Void> alterarSenha(@RequestBody @Valid AlterarSenhaDTO alterarSenhaDTO) {
    //     //TODO: process POST request
        
    //     return null;
    // }   
    
}
