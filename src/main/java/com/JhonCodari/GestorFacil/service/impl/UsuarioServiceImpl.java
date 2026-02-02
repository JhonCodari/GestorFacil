package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.JhonCodari.GestorFacil.dto.UsuarioCadastroDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;
import com.JhonCodari.GestorFacil.exception.EmailJaCadastradoException;
import com.JhonCodari.GestorFacil.service.UsuarioService;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.model.Usuario;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioRespostaDTO cadastrarUsuario(UsuarioCadastroDTO usuarioCadastroDTO) {

        if (this.usuarioRepository.existsByEmailValor(usuarioCadastroDTO.emailValor())) 
            throw new EmailJaCadastradoException("Este E-mail já está em uso.");

        String senhaHash = passwordEncoder.encode(usuarioCadastroDTO.senhaValor());

        Usuario usuario = new Usuario(
            usuarioCadastroDTO.nomeCompleto(),
            usuarioCadastroDTO.email(),
            senhaHash
        );
        Usuario usuarioSalvo = this.usuarioRepository.save(usuario);
        return new UsuarioRespostaDTO(
            usuarioSalvo.getId(),
            usuarioSalvo.getNomeCompleto(),
            usuarioSalvo.getEmail()
        );
    }
    
}
