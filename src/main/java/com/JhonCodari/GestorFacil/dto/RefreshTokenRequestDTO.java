package com.JhonCodari.GestorFacil.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import com.JhonCodari.GestorFacil.model.valueobjects.RefreshToken;

public record RefreshTokenRequestDTO(

    @Valid
    RefreshToken refreshToken,

    @NotBlank
    Long userId
) {
    
}
