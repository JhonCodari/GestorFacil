package com.JhonCodari.GestorFacil.dto;

import com.JhonCodari.GestorFacil.model.NomeCompleto;
import com.JhonCodari.GestorFacil.model.EmailUsuario;

public record UsuarioCadastroDTO(
    NomeCompleto nomeCompleto,  
    EmailUsuario email,
    String senha
) {
    public String emailValor() {
        return email.valor();
    }
}