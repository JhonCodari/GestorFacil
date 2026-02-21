package com.JhonCodari.GestorFacil.dto;

import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;

public record RecuperacaoSenhaRequestDTO(
    EmailUsuario email
) {
    public String getEmailUsuario() {
        return email.valor();
    }
}
