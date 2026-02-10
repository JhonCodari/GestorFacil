package com.JhonCodari.GestorFacil.model.valueobjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record Token (
    @NotBlank(message = "O token não pode estar em branco.")
    @Size(min = 32, max = 512, message = "O token não pode conter menos de 32 caracteres nem exceder 512 caracteres.")
    @Pattern(
        regexp = "^(Bearer )?[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$",
        message = "O token deve estar no formato JWT válido, opcionalmente precedido por 'Bearer '."
    )
    String valor

) {   
    public Token {
        if (valor.startsWith("Bearer ")) valor = valor.substring(7);
    }

    public String comPrefixoBearer() {
        return this.valor.startsWith("Bearer ") ? this.valor : "Bearer " + this.valor;
    }   
}
