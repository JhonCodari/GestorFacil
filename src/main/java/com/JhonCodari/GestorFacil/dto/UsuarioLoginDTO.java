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
    public String emailValor() {
        return email.valor();
    }

    public String senhaValor() {
        return senha.valor();
    }
}