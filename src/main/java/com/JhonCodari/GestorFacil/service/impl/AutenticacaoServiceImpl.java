package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.stereotype.Service;

import com.JhonCodari.GestorFacil.config.JwtTokenProvider;
import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
import com.JhonCodari.GestorFacil.service.AutenticacaoService;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.exception.CredenciaisInvalidasException;

@Service
public class AutenticacaoServiceImpl implements AutenticacaoService {

    private UsuarioRepository usuarioRepository;
    private JwtTokenProvider jwtTokenProvider;

    public AutenticacaoServiceImpl(UsuarioRepository usuarioRepository, JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String autenticar(UsuarioLoginDTO usuarioLogin) {
        var usuarioCadastrado = usuarioRepository.findByEmailValor(usuarioLogin.getEnderecoEmail());
        if(usuarioLogin.senha().confere(usuarioCadastrado.getSenhaHash()))
            return jwtTokenProvider.gerarToken(usuarioCadastrado.getEnderecoEmail());
        
        throw new CredenciaisInvalidasException("usuario ou senha inválidos.");        
    }
}
