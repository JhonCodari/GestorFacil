package com.JhonCodari.GestorFacil.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecuperacaoSenhaRequestDTO(
    @NotBlank(message = "Email não pode ser vazio")
    @Email(message = "Email inválido")
    String email
) {}
