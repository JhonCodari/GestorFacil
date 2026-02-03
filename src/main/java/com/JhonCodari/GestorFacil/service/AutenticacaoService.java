package com.JhonCodari.GestorFacil.service;

import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;

public interface AutenticacaoService {

    public String autenticar(UsuarioLoginDTO usuarioLoginDTO);
    
}
