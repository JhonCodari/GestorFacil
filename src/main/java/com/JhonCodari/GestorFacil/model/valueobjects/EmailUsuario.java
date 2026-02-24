package com.JhonCodari.GestorFacil.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;

@Embeddable
public record EmailUsuario(
    @NotBlank(message = "O email não pode estar em branco.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    @Email(message = "O email dever ser um endereco valido")
    String valor
) {}

