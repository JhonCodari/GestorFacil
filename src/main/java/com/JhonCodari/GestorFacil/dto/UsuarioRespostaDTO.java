package com.JhonCodari.GestorFacil.dto;

import com.JhonCodari.GestorFacil.model.valueobjects.*;

public record UsuarioRespostaDTO (
    Long id,
    NomeCompleto nomeCompleto,
    EmailUsuario email
) {}
