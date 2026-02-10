package com.JhonCodari.GestorFacil.service;

import com.JhonCodari.GestorFacil.dto.UsuarioCadastroDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;

public interface UsuarioService {

    UsuarioRespostaDTO cadastrarUsuario(UsuarioCadastroDTO usuarioCadastroDTO);

    UsuarioRespostaDTO consultarUsuarioPorEmail(EmailUsuario email);
    
}
