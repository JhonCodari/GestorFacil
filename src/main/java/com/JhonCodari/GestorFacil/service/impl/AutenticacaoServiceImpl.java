package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.JhonCodari.GestorFacil.config.JwtTokenProvider;
import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
import com.JhonCodari.GestorFacil.service.AutenticacaoService;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.model.Usuario;
import com.JhonCodari.GestorFacil.exception.CredenciaisInvalidasException;


@Service
public class AutenticacaoServiceImpl implements AutenticacaoService {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

    public AutenticacaoServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String autenticar(UsuarioLoginDTO usuarioLoginDTO) {
        Usuario usuario = usuarioRepository.findByEmailValor(usuarioLoginDTO.emailValor());
        boolean senhaValida = passwordEncoder.matches(usuarioLoginDTO.senhaValor(), usuario.getSenhaHash());
        if (!senhaValida) throw new CredenciaisInvalidasException("usuario ou senha inválidos.");
        String token = jwtTokenProvider.gerarToken(usuario.getEmail().valor());
        return token;
    }
}
