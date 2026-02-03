package com.JhonCodari.GestorFacil.dto;

import com.JhonCodari.GestorFacil.model.EmailUsuario;
import com.JhonCodari.GestorFacil.model.Senha;

import jakarta.validation.Valid;

public record UsuarioLoginDTO(
    
    @Valid
    EmailUsuario email,

    @Valid
    Senha senha
) {
    public String getEnderecoEmail() {
        return email.valor();
    }

    public String getSenhaValor() {
        return senha.valor();
    }
}