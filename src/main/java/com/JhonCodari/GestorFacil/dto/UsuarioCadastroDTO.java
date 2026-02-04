package com.JhonCodari.GestorFacil.dto;

import jakarta.validation.Valid;
import com.JhonCodari.GestorFacil.model.valueobjects.*;

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