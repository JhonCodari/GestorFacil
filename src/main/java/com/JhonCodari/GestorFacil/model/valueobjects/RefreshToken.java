package com.JhonCodari.GestorFacil.model.valueobjects;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RefreshToken(

    @NotBlank(message = "O refreshToken não pode estar em branco.")
    @Size(min = 32, max = 512, message = "O token não pode conter menos de 32 caracteres nem exceder 512 caracteres.")
    @Pattern(
        regexp = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$",
        message = "O refreshToken deve estar no formato JWT válido."
    )
    String valor
) {

    
}
