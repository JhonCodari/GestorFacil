package com.JhonCodari.GestorFacil.dto;

import com.JhonCodari.GestorFacil.model.NomeCompleto;
import com.JhonCodari.GestorFacil.model.EmailUsuario;

public record UsuarioRespostaDTO (
    Long id,
    NomeCompleto nomeCompleto,
    EmailUsuario email
) {}
