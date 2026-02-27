package com.JhonCodari.GestorFacil.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Health Check", description = "Verificacao de saude da aplicacao")
public class HealthCheckController {

    @GetMapping("/health")
    @Operation(summary = "Verificar status da aplicacao")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "GestorFacil API"
        ));
    }
}
