package com.JhonCodari.GestorFacil.controller;

import com.JhonCodari.GestorFacil.dto.UsuarioAtualizacaoDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioCadastroDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;
import com.JhonCodari.GestorFacil.mapper.UsuarioMapper;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.JhonCodari.GestorFacil.service.UsuarioService;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuarios", description = "Gerenciamento de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar perfil do usuario autenticado")
    public ResponseEntity<UsuarioRespostaDTO> consultar(Principal principal) {
        var email = new EmailUsuario(principal.getName());
        return ResponseEntity.ok(
            UsuarioMapper.toDTO(this.usuarioService.consultarUsuarioPorEmail(email))
        );
    } 
    
    @PostMapping("/cadastro")
    @Operation(summary = "Cadastrar novo usuario")
    public ResponseEntity<UsuarioRespostaDTO> cadastro(@RequestBody @Valid UsuarioCadastroDTO usuarioCadastroDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            this.usuarioService.cadastrarUsuario(usuarioCadastroDTO)
        );
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Atualizar dados do usuario autenticado")
    public ResponseEntity<UsuarioRespostaDTO> atualizar(
            Principal principal,
            @RequestBody @Valid UsuarioAtualizacaoDTO dados) {
        var email = new EmailUsuario(principal.getName());
        return ResponseEntity.ok(this.usuarioService.atualizarUsuario(email, dados));
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Excluir conta do usuario autenticado")
    public ResponseEntity<Void> excluir(Principal principal) {
        var email = new EmailUsuario(principal.getName());
        this.usuarioService.excluirUsuario(email);
        return ResponseEntity.noContent().build();
    }
}
