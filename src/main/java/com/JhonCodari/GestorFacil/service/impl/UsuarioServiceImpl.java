package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.stereotype.Service;

import com.JhonCodari.GestorFacil.dto.UsuarioCadastroDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;
import com.JhonCodari.GestorFacil.exception.EmailJaCadastradoException;
import com.JhonCodari.GestorFacil.service.UsuarioService;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.model.Usuario;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UsuarioRespostaDTO cadastrarUsuario(UsuarioCadastroDTO usuarioCadastroDTO) {

        if (this.usuarioRepository.existsByEmailValor(usuarioCadastroDTO.emailValor())) 
            throw new EmailJaCadastradoException("Este E-mail já está em uso.");

        var usuario = new Usuario(
            usuarioCadastroDTO.nomeCompleto(),
            usuarioCadastroDTO.email(),
            usuarioCadastroDTO.senha()
        );
        var usuarioSalvo = this.usuarioRepository.save(usuario);
        return new UsuarioRespostaDTO(
            usuarioSalvo.getId(),
            usuarioSalvo.getNomeCompleto(),
            usuarioSalvo.getEmail()
        );
    }

    @Override
    public UsuarioRespostaDTO consultarUsuarioPorEmail (EmailUsuario email) {
        if (!this.usuarioRepository.existsByEmailValor(email.valor())) 
            throw new EmailJaCadastradoException("Usuário não encontrado.");

        var usuario = this.usuarioRepository.findByEmailValor(email.valor());
        return new UsuarioRespostaDTO(
            usuario.getId(),
            usuario.getNomeCompleto(),
            usuario.getEmail()
        );
    }
    
}
