package com.JhonCodari.GestorFacil.controller;

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
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioRespostaDTO> consultar(Principal principal) {
        var email = new EmailUsuario(principal.getName());
        return ResponseEntity.ok(
            UsuarioMapper.toDTO(this.usuarioService.consultarUsuarioPorEmail(email))
        );
    } 
    
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioRespostaDTO> cadastro(@RequestBody @Valid UsuarioCadastroDTO usuarioCadastroDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            this.usuarioService.cadastrarUsuario(usuarioCadastroDTO)
        );
    }     
}
