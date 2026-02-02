package com.JhonCodari.GestorFacil.service;

import com.JhonCodari.GestorFacil.dto.UsuarioCadastroDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;

public interface UsuarioService {

    UsuarioRespostaDTO cadastrarUsuario(UsuarioCadastroDTO usuarioCadastroDTO);
    
}
