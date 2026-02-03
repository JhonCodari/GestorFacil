package com.JhonCodari.GestorFacil.model;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record Senha(
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, max = 50, message = "A senha deve ter entre 8 e 50 caracteres.")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&].+$",
        message = "A senha deve conter pelo menos uma letra maiuscula, uma letra minuscula, um numero e um caractere especial."
    )
    String valor
) {

    private static final PasswordEncoder codificador = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    
    public String hash() {
        return codificador.encode(valor);
    }

    public boolean confere(String hashArmazenado) {
        return codificador.matches(valor, hashArmazenado);
    }
}
