package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import com.JhonCodari.GestorFacil.config.JwtTokenProvider;
import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
import com.JhonCodari.GestorFacil.service.AutenticacaoService;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.exception.CredenciaisInvalidasException;
import com.JhonCodari.GestorFacil.model.valueobjects.Token;

@Service
public class AutenticacaoServiceImpl implements AutenticacaoService {

    private final Set<String> tokensInvalidos = new HashSet<>();

    private UsuarioRepository usuarioRepository;
    private JwtTokenProvider jwtTokenProvider;

    public AutenticacaoServiceImpl(UsuarioRepository usuarioRepository, JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String autenticar(UsuarioLoginDTO usuarioLogin) {
        var usuarioCadastrado = usuarioRepository.findByEmailValor(usuarioLogin.getEnderecoEmail());
        if(usuarioLogin.senha().confere(usuarioCadastrado.getSenhaHash()))
            return jwtTokenProvider.gerarToken(usuarioCadastrado.getEmail());
        
        throw new CredenciaisInvalidasException("usuario ou senha inválidos.");        
    }    

    @Override
    public String invalidarToken(Token token) {
        if (tokensInvalidos.contains(token.semPrefixoBearer())) 
            throw new IllegalArgumentException("Token inválido.");

        tokensInvalidos.add(token.semPrefixoBearer());
        return "Logout Realizado com sucesso!.";
    }
}
