package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.JhonCodari.GestorFacil.config.JwtTokenProvider;
import com.JhonCodari.GestorFacil.model.valueobjects.AccessToken;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.model.valueobjects.RefreshToken;
import com.JhonCodari.GestorFacil.service.AccessTokenService;
import com.JhonCodari.GestorFacil.service.RefreshTokenService;
import com.JhonCodari.GestorFacil.service.UsuarioService;

import java.util.concurrent.TimeUnit;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final StringRedisTemplate redisTemplate;

    public AccessTokenServiceImpl(
            UsuarioService usuarioService,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenService refreshTokenService,
            StringRedisTemplate redisTemplate) {
        this.usuarioService = usuarioService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public AccessToken criar(EmailUsuario usuarioEmail) {
        usuarioService.consultarUsuarioPorEmail(usuarioEmail);        
        var tokenJwt = jwtTokenProvider.gerarAccessToken(usuarioEmail);        
        return new AccessToken(tokenJwt);
    }

    @Override
    public boolean validar(AccessToken accessToken) {
        if (isTokenRevogado(accessToken)) {
            return false;
        }
        
        if (!jwtTokenProvider.validarToken(accessToken.valor())) {
            return false;
        }
        
        var emailExtraido = jwtTokenProvider.extrairSubject(accessToken.valor());
        var emailUsuario = new EmailUsuario(emailExtraido);
        
        usuarioService.consultarUsuarioPorEmail(emailUsuario);
        
        return true;
    }

    @Override
    public AccessToken renovar(RefreshToken refreshToken) {
        if (!refreshTokenService.validar(refreshToken)) {
            throw new IllegalArgumentException("Refresh token inválido");
        }
        
        var emailExtraido = jwtTokenProvider.extrairSubject(refreshToken.valor());
        var emailUsuario = new EmailUsuario(emailExtraido);
        
        return criar(emailUsuario);
    }

    @Override
    public void revogar(AccessToken accessToken) {
        var tempoExpiracao = jwtTokenProvider.getTempoExpiracao(accessToken.valor());
        
        redisTemplate.opsForValue().set(
            accessToken.valor(),
            "revogado",
            tempoExpiracao,
            TimeUnit.MILLISECONDS
        );
    }

    private boolean isTokenRevogado(AccessToken accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(accessToken.valor()));
    }
    
}
