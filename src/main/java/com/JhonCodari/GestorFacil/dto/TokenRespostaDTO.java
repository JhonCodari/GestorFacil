package com.JhonCodari.GestorFacil.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRespostaDTO(
    @NotBlank(message = "O accessToken não pode estar em branco.")
    String accessToken,
    
    @NotBlank(message = "O refreshToken não pode estar em branco.")
    String refreshToken,
    
    @NotBlank(message = "O tipo do token não pode estar em branco.")
    String tokenType
) {
    public TokenRespostaDTO(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}
