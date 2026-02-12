package com.JhonCodari.GestorFacil.service.impl;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.JhonCodari.GestorFacil.config.JwtTokenProvider;
import com.JhonCodari.GestorFacil.dto.RefreshTokenRequestDTO;
import com.JhonCodari.GestorFacil.model.RefreshTokenEntity;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.model.valueobjects.RefreshToken;
import com.JhonCodari.GestorFacil.repository.RefreshTokenRepository;
import com.JhonCodari.GestorFacil.service.RefreshTokenService;
import com.JhonCodari.GestorFacil.service.UsuarioService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    
    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(UsuarioService usuarioService, JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository) {
        this.usuarioService = usuarioService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshToken criar(EmailUsuario usuarioEmail) {
        var usuario = usuarioService.consultarUsuarioPorEmail(usuarioEmail);

        var refreshToken = new RefreshToken(
            jwtTokenProvider.gerarRefreshToken(usuarioEmail)
        );        

        var entidadeRefreshToken = new RefreshTokenEntity(
            refreshToken,
            usuario,
            jwtTokenProvider.getDataExpiracao(refreshToken.valor())        
        );

        refreshTokenRepository.save(entidadeRefreshToken);
        return refreshToken;
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
