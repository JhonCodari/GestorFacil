package com.JhonCodari.GestorFacil.controller;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.JhonCodari.GestorFacil.dto.*;
import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;
import com.JhonCodari.GestorFacil.service.ImportacaoExcelService;
import com.JhonCodari.GestorFacil.service.TransacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transacoes")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Transacoes", description = "CRUD de transacoes financeiras, filtros, conversao e importacao")
public class TransacaoController {

    private final TransacaoService transacaoService;
    private final ImportacaoExcelService importacaoExcelService;

    public TransacaoController(TransacaoService transacaoService, ImportacaoExcelService importacaoExcelService) {
        this.transacaoService = transacaoService;
        this.importacaoExcelService = importacaoExcelService;
    }

    @PostMapping
    @Operation(summary = "Criar nova transacao")
    public ResponseEntity<TransacaoRespostaDTO> criar(
            @RequestBody @Valid TransacaoCadastroDTO dto,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(transacaoService.criar(dto, principal.getName()));
    }

    @GetMapping
    @Operation(summary = "Listar transacoes com filtros opcionais por tipo, categoria e periodo")
    public ResponseEntity<Page<TransacaoRespostaDTO>> listar(
            @RequestParam(required = false) TipoTransacao tipo,
            @RequestParam(required = false) CategoriaTransacao categoria,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Pageable pageable,
            Principal principal) {
        if (tipo != null || categoria != null || dataInicio != null || dataFim != null) {
            return ResponseEntity.ok(
                transacaoService.filtrar(principal.getName(), tipo, categoria, dataInicio, dataFim, pageable)
            );
        }
        return ResponseEntity.ok(transacaoService.listar(principal.getName(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar transacao por ID")
    public ResponseEntity<TransacaoRespostaDTO> buscarPorId(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(transacaoService.buscarPorId(id, principal.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar transacao (parcial)")
    public ResponseEntity<TransacaoRespostaDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid TransacaoAtualizacaoDTO dto,
            Principal principal) {
        return ResponseEntity.ok(transacaoService.atualizar(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar transacao por ID")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            Principal principal) {
        transacaoService.deletar(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/convertidas")
    @Operation(summary = "Listar transacoes com valor convertido para moeda estrangeira")
    public ResponseEntity<Page<TransacaoConvertidaRespostaDTO>> listarConvertidas(
            @RequestParam String moeda,
            Pageable pageable,
            Principal principal) {
        return ResponseEntity.ok(
            transacaoService.listarConvertidas(principal.getName(), moeda, pageable)
        );
    }

    @GetMapping("/{id}/convertida")
    @Operation(summary = "Buscar transacao por ID com conversao de moeda")
    public ResponseEntity<TransacaoConvertidaRespostaDTO> buscarPorIdConvertida(
            @PathVariable Long id,
            @RequestParam String moeda,
            Principal principal) {
        return ResponseEntity.ok(
            transacaoService.buscarPorIdConvertida(id, principal.getName(), moeda)
        );
    }

    @PostMapping("/importar")
    @Operation(summary = "Importar transacoes em massa via arquivo Excel (.xlsx)")
    public ResponseEntity<ImportacaoResultadoDTO> importar(
            @RequestParam("arquivo") org.springframework.web.multipart.MultipartFile arquivo,
            Principal principal) {
        return ResponseEntity.ok(importacaoExcelService.importarTransacoes(arquivo, principal.getName()));
    }
}
