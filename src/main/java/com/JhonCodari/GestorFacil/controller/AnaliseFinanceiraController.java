package com.JhonCodari.GestorFacil.controller;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.JhonCodari.GestorFacil.dto.AnaliseFinanceiraConvertidaRespostaDTO;
import com.JhonCodari.GestorFacil.dto.AnaliseFinanceiraRespostaDTO;
import com.JhonCodari.GestorFacil.service.AnaliseFinanceiraService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/analises")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Analise Financeira", description = "Analise de receitas, despesas e saldo por periodo")
public class AnaliseFinanceiraController {

    private final AnaliseFinanceiraService analiseFinanceiraService;

    public AnaliseFinanceiraController(AnaliseFinanceiraService analiseFinanceiraService) {
        this.analiseFinanceiraService = analiseFinanceiraService;
    }

    @GetMapping("/financeira")
    @Operation(summary = "Analisar financas por periodo (em BRL)")
    public ResponseEntity<AnaliseFinanceiraRespostaDTO> analisar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFim,

            Principal principal
        ) {
        return ResponseEntity.ok(
            analiseFinanceiraService.analisar(principal.getName(), dataInicio, dataFim)
        );
    }

    @GetMapping("/financeira/convertida")
    @Operation(summary = "Analisar financas por periodo com conversao de moeda")
    public ResponseEntity<AnaliseFinanceiraConvertidaRespostaDTO> analisarConvertida(
            @RequestParam String moeda,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFim,

            Principal principal
        ) {
        return ResponseEntity.ok(
            analiseFinanceiraService.analisarConvertida(principal.getName(), dataInicio, dataFim, moeda)
        );
    }
}
