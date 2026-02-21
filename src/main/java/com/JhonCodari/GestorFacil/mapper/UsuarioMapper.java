package com.JhonCodari.GestorFacil.mapper;

import com.JhonCodari.GestorFacil.model.UsuarioEntity;
import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;

public class UsuarioMapper {

    public static UsuarioRespostaDTO toDTO(UsuarioEntity usuario) {
        return new UsuarioRespostaDTO(
            usuario.getId(),
            usuario.getNomeCompleto(),
            usuario.getEmail()
        );
    }
    
}
