package com.JhonCodari.GestorFacil.dto;

import com.JhonCodari.GestorFacil.model.valueobjects.Senha;
import jakarta.validation.Valid;

public record RedefinirSenhaRequestDTO(
    @Valid
    Senha novaSenha
) {}
