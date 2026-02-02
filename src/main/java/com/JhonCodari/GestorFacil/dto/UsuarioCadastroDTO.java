package com.JhonCodari.GestorFacil.dto;

import com.JhonCodari.GestorFacil.model.NomeCompleto;
import com.JhonCodari.GestorFacil.model.Senha;
import com.JhonCodari.GestorFacil.model.EmailUsuario;

import jakarta.validation.Valid;

public record UsuarioCadastroDTO(

    @Valid
    NomeCompleto nomeCompleto,

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