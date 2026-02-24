package com.JhonCodari.GestorFacil.service.impl;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.JhonCodari.GestorFacil.config.JwtTokenProvider;
import com.JhonCodari.GestorFacil.exception.RefreshTokenNaoEncontradoException;
import com.JhonCodari.GestorFacil.exception.RefreshTokenRevogadoException;
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
    @Transactional
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
        if (!jwtTokenProvider.validarToken(refreshToken.valor())) return false;
        
        var entidade = refreshTokenRepository.findByRefreshToken_valor(refreshToken.valor());
        
        if (entidade == null) throw new RefreshTokenNaoEncontradoException("Refresh token não encontrado no banco de dados");
        
        if (entidade.isRevogado()) throw new RefreshTokenRevogadoException("Refresh token foi revogado");
        
        var emailExtraido = jwtTokenProvider.extrairSubject(refreshToken.valor());
        var emailUsuario = new EmailUsuario(emailExtraido);
        
        usuarioService.consultarUsuarioPorEmail(emailUsuario);
        
        return true;
    }

    @Override
    @Transactional
    public RefreshToken rotacionar(RefreshToken refreshToken) {
        validar(refreshToken);
        
        var entidadeAntiga = refreshTokenRepository.findByRefreshToken_valor(
            refreshToken.valor()
        );
        
        if (entidadeAntiga == null) throw new RefreshTokenNaoEncontradoException("Refresh token não encontrado");
        
        
        entidadeAntiga.revogar();
        refreshTokenRepository.save(entidadeAntiga);
        
        var emailExtraido = jwtTokenProvider.extrairSubject(refreshToken.valor());
        var emailUsuario = new EmailUsuario(emailExtraido);
        
        return criar(emailUsuario);
    }

    @Override
    @Transactional
    public void revogar(RefreshToken refreshToken) {
        var entidade = refreshTokenRepository.findByRefreshToken_valor(refreshToken.valor());
        
        if (entidade == null) throw new RefreshTokenNaoEncontradoException("Refresh token não encontrado");     
        
        entidade.revogar();
        refreshTokenRepository.save(entidade);
    }

    @Override
    @Transactional
    public void revogarTodosRefreshTokensDoUsuario(String email) {
        var tokens = refreshTokenRepository.findAllByUsuario_Email_Valor(email);
        
        tokens.forEach(token -> {
            if (!token.isRevogado()) {
                token.revogar();
                refreshTokenRepository.save(token);
            }
        });
    }
    
}
