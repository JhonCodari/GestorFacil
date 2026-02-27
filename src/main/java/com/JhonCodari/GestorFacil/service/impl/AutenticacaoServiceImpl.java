package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.stereotype.Service;

import com.JhonCodari.GestorFacil.config.JwtTokenProvider;
import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
import com.JhonCodari.GestorFacil.service.AutenticacaoService;
import com.JhonCodari.GestorFacil.service.AccessTokenService;
import com.JhonCodari.GestorFacil.service.RefreshTokenService;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.exception.CredenciaisInvalidasException;
import com.JhonCodari.GestorFacil.exception.EmailNaoVerificadoException;
import com.JhonCodari.GestorFacil.model.valueobjects.Token;
import com.JhonCodari.GestorFacil.model.valueobjects.AccessToken;

@Service
public class AutenticacaoServiceImpl implements AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    public AutenticacaoServiceImpl(
            UsuarioRepository usuarioRepository,
            AccessTokenService accessTokenService,
            RefreshTokenService refreshTokenService,
            JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String autenticar(UsuarioLoginDTO usuarioLogin) {
        var usuarioCadastrado = usuarioRepository.findByEmailValor(usuarioLogin.getEnderecoEmail());
        
        if (usuarioCadastrado == null)
            throw new CredenciaisInvalidasException("usuario ou senha invalidos.");
        
        if (!usuarioCadastrado.isEmailVerificado()) 
            throw new EmailNaoVerificadoException("Email nao verificado. Verifique seu email antes de fazer login.");
        
        if(usuarioLogin.senha().confere(usuarioCadastrado.getSenhaHash()))
            return accessTokenService.criar(usuarioCadastrado.getEmail()).valor();
        
        throw new CredenciaisInvalidasException("usuario ou senha invalidos.");
    }

    @Override
    public String invalidarToken(Token token) {
        var accessToken = new AccessToken(token.valor());
        var email = jwtTokenProvider.extrairSubject(accessToken.valor());
        
        accessTokenService.revogar(accessToken);
        refreshTokenService.revogarTodosRefreshTokensDoUsuario(email);
        
        return "Logout Realizado com sucesso!.";
    }
}

