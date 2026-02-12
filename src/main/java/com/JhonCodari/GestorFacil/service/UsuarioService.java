package com.JhonCodari.GestorFacil.service;

import com.JhonCodari.GestorFacil.dto.UsuarioCadastroDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.model.Usuario;

public interface UsuarioService {

    UsuarioRespostaDTO cadastrarUsuario(UsuarioCadastroDTO usuarioCadastroDTO);

    Usuario consultarUsuarioPorEmail(EmailUsuario email);
    
}
