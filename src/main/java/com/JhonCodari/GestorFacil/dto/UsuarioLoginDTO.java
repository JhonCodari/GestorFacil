package com.JhonCodari.GestorFacil.dto;

import jakarta.validation.Valid;
import com.JhonCodari.GestorFacil.model.valueobjects.*;

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