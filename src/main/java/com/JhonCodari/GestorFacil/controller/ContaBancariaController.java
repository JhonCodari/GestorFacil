package com.JhonCodari.GestorFacil.controller;

import java.security.Principal;
import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.JhonCodari.GestorFacil.dto.ContaBancariaRespostaDTO;
import com.JhonCodari.GestorFacil.dto.ContaBancariaVinculoDTO;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.service.ContaBancariaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/conta-bancaria")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Conta Bancaria", description = "Vinculo e consulta de conta bancaria externa")
public class ContaBancariaController {

    private final ContaBancariaService contaBancariaService;

    public ContaBancariaController(ContaBancariaService contaBancariaService) {
        this.contaBancariaService = contaBancariaService;
    }

    @PostMapping("/vincular")
    @Operation(summary = "Vincular conta bancaria ao usuario")
    public ResponseEntity<String> vincular(
            @RequestBody @Valid ContaBancariaVinculoDTO dto,
            Principal principal) {
        contaBancariaService.vincular(
            new EmailUsuario(principal.getName()),
            dto.idConta());
        return ResponseEntity.ok("Conta bancaria vinculada com sucesso.");
    }

    @DeleteMapping("/desvincular")
    @Operation(summary = "Desvincular conta bancaria do usuario")
    public ResponseEntity<Void> desvincular(Principal principal) {
        contaBancariaService.desvincular(new EmailUsuario(principal.getName()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/saldo")
    @Operation(summary = "Consultar saldo da conta bancaria vinculada")
    public ResponseEntity<ContaBancariaRespostaDTO> consultarSaldo(Principal principal) {
        return ResponseEntity.ok(
            contaBancariaService.consultarSaldo(new EmailUsuario(principal.getName()))
        );
    }
}
