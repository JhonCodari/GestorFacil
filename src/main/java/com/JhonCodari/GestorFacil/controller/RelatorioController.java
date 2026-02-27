package com.JhonCodari.GestorFacil.controller;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.JhonCodari.GestorFacil.service.RelatorioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/relatorios")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Relatorios", description = "Download de relatorios financeiros em Excel")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/financeiro")
    @Operation(summary = "Baixar relatorio financeiro em formato Excel por periodo")
    public ResponseEntity<byte[]> baixarRelatorio(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Principal principal) {

        byte[] arquivo = relatorioService.gerarRelatorioExcel(principal.getName(), dataInicio, dataFim);

        String nomeArquivo = "relatorio-financeiro-" + dataInicio + "-a-" + dataFim + ".xlsx";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nomeArquivo)
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .contentLength(arquivo.length)
            .body(arquivo);
    }
}
