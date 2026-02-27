package com.JhonCodari.GestorFacil.dto;

import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.model.valueobjects.NomeCompleto;

import jakarta.validation.Valid;

public record UsuarioAtualizacaoDTO(
    @Valid
    NomeCompleto nomeCompleto,

    @Valid
    EmailUsuario email
) {}
