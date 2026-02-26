package com.JhonCodari.GestorFacil.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.JhonCodari.GestorFacil.dto.ContaBancariaRespostaDTO;
import com.JhonCodari.GestorFacil.dto.ContaBancariaVinculoDTO;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.service.ContaBancariaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/conta-bancaria")
@PreAuthorize("isAuthenticated()")
public class ContaBancariaController {

    private final ContaBancariaService contaBancariaService;

    public ContaBancariaController(ContaBancariaService contaBancariaService) {
        this.contaBancariaService = contaBancariaService;
    }

    @PostMapping("/vincular")
    public ResponseEntity<String> vincular(
            @RequestBody @Valid ContaBancariaVinculoDTO dto,
            Principal principal) {
        contaBancariaService.vincular(
            new EmailUsuario(principal.getName()),
            dto.idConta());
        return ResponseEntity.ok("Conta bancaria vinculada com sucesso.");
    }

    @DeleteMapping("/desvincular")
    public ResponseEntity<Void> desvincular(Principal principal) {
        contaBancariaService.desvincular(new EmailUsuario(principal.getName()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/saldo")
    public ResponseEntity<ContaBancariaRespostaDTO> consultarSaldo(Principal principal) {
        return ResponseEntity.ok(
            contaBancariaService.consultarSaldo(new EmailUsuario(principal.getName()))
        );
    }
}
