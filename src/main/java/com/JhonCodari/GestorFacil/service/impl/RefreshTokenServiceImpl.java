package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.stereotype.Service;

import com.JhonCodari.GestorFacil.config.JwtTokenProvider;
import com.JhonCodari.GestorFacil.dto.RefreshTokenRequestDTO;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.model.valueobjects.RefreshToken;
import com.JhonCodari.GestorFacil.service.RefreshTokenService;
import com.JhonCodari.GestorFacil.service.UsuarioService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    
    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshTokenServiceImpl(UsuarioService usuarioService, JwtTokenProvider jwtTokenProvider) {
        this.usuarioService = usuarioService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public RefreshToken criar(EmailUsuario usuarioEmail) {
        usuarioService.consultarUsuarioPorEmail(usuarioEmail);
        
        var tokenJwt = jwtTokenProvider.gerarRefreshToken(usuarioEmail);
        
        return new RefreshToken(tokenJwt);
    }

    @Override
    public boolean validar(RefreshToken refreshToken) {
        if (!jwtTokenProvider.validarToken(refreshToken.valor())) {
            return false;
        }
        
        var emailExtraido = jwtTokenProvider.extrairSubject(refreshToken.valor());
        var emailUsuario = new EmailUsuario(emailExtraido);
        
        usuarioService.consultarUsuarioPorEmail(emailUsuario);
        
        return true;
    }

    @Override
    public void rotacionar(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        validar(refreshTokenRequestDTO.refreshToken());
        
        var emailExtraido = jwtTokenProvider.extrairSubject(refreshTokenRequestDTO.refreshToken().valor());
        var emailUsuario = new EmailUsuario(emailExtraido);
        
        criar(emailUsuario);
    }

    @Override
    public void revogar(RefreshToken refreshToken) {
        validar(refreshToken);
    }
    
}
