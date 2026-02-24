package com.JhonCodari.GestorFacil.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.JhonCodari.GestorFacil.dto.TransacaoAtualizacaoDTO;
import com.JhonCodari.GestorFacil.dto.TransacaoCadastroDTO;
import com.JhonCodari.GestorFacil.dto.TransacaoRespostaDTO;
import com.JhonCodari.GestorFacil.service.TransacaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transacoes")
@PreAuthorize("isAuthenticated()")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    public ResponseEntity<TransacaoRespostaDTO> criar(
            @RequestBody @Valid TransacaoCadastroDTO dto,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(transacaoService.criar(dto, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<Page<TransacaoRespostaDTO>> listar(
            Pageable pageable,
            Principal principal) {
        return ResponseEntity.ok(transacaoService.listar(principal.getName(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoRespostaDTO> buscarPorId(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(transacaoService.buscarPorId(id, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoRespostaDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid TransacaoAtualizacaoDTO dto,
            Principal principal) {
        return ResponseEntity.ok(transacaoService.atualizar(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            Principal principal) {
        transacaoService.deletar(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
