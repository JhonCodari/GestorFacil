package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.stereotype.Service;

import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
import com.JhonCodari.GestorFacil.service.AutenticacaoService;
import com.JhonCodari.GestorFacil.service.AccessTokenService;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.exception.CredenciaisInvalidasException;
import com.JhonCodari.GestorFacil.model.valueobjects.Token;
import com.JhonCodari.GestorFacil.model.valueobjects.AccessToken;

@Service
public class AutenticacaoServiceImpl implements AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final AccessTokenService accessTokenService;

    public AutenticacaoServiceImpl(UsuarioRepository usuarioRepository, AccessTokenService accessTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.accessTokenService = accessTokenService;
    }

    public String autenticar(UsuarioLoginDTO usuarioLogin) {
        var usuarioCadastrado = usuarioRepository.findByEmailValor(usuarioLogin.getEnderecoEmail());
        if(usuarioLogin.senha().confere(usuarioCadastrado.getSenhaHash()))
            return accessTokenService.criar(usuarioCadastrado.getEmail()).valor();
        
        throw new CredenciaisInvalidasException("usuario ou senha inválidos.");
    }

    @Override
    public String invalidarToken(Token token) {
        var accessToken = new AccessToken(token.valor());
        accessTokenService.revogar(accessToken);
        return "Logout Realizado com sucesso!.";
    }
}

