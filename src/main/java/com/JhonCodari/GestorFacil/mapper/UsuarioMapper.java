package com.JhonCodari.GestorFacil.mapper;

import com.JhonCodari.GestorFacil.model.Usuario;
import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;

public class UsuarioMapper {

    public static UsuarioRespostaDTO toDTO(Usuario usuario) {
        return new UsuarioRespostaDTO(
            usuario.getId(),
            usuario.getNomeCompleto(),
            usuario.getEmail()
        );
    }
    
}
