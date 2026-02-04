package com.JhonCodari.GestorFacil.service;

import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
import com.JhonCodari.GestorFacil.model.valueobjects.Token;

public interface AutenticacaoService {

    public String autenticar(UsuarioLoginDTO usuarioLoginDTO);

    public String invalidarToken(Token token);
    
}
