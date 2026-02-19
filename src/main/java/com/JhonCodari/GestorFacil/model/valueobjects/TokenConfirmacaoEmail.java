package com.JhonCodari.GestorFacil.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

@Embeddable
public record TokenConfirmacaoEmail(
    @NotBlank(message = "Token de confirmação não pode ser vazio")
    @Pattern(
        regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
        message = "Formato de token de confirmação inválido"
    )
    String valor
) {
    public static TokenConfirmacaoEmail gerar() {
        return new TokenConfirmacaoEmail(UUID.randomUUID().toString());
    }
}
