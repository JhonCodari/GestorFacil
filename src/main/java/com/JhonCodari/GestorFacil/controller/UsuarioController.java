package com.JhonCodari.GestorFacil.controller;

import org.springframework.web.bind.annotation.RestController;

import com.JhonCodari.GestorFacil.dto.UsuarioCadastroDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;
import com.JhonCodari.GestorFacil.mapper.UsuarioMapper;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;

import jakarta.validation.Valid;
import com.JhonCodari.GestorFacil.service.UsuarioService;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(
            this.usuarioService.cadastrarUsuario(usuarioCadastroDTO)
        );
    } 
    
    @GetMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioRespostaDTO> meuPerfil(Principal principal) {
        var email = new EmailUsuario(principal.getName());
        return ResponseEntity.ok(
            UsuarioMapper.toDTO(this.usuarioService.consultarUsuarioPorEmail(email))
        );
    }     
}
