package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

import com.JhonCodari.GestorFacil.config.JwtTokenProvider;
import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
import com.JhonCodari.GestorFacil.service.AutenticacaoService;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.exception.CredenciaisInvalidasException;
import com.JhonCodari.GestorFacil.model.valueobjects.Token;

@Service
public class AutenticacaoServiceImpl implements AutenticacaoService {

    private StringRedisTemplate redisTemplate;
    private UsuarioRepository usuarioRepository;
    private JwtTokenProvider jwtTokenProvider;

    public AutenticacaoServiceImpl(UsuarioRepository usuarioRepository, JwtTokenProvider jwtTokenProvider, StringRedisTemplate redisTemplate) {
        this.usuarioRepository = usuarioRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    public String autenticar(UsuarioLoginDTO usuarioLogin) {
        var usuarioCadastrado = usuarioRepository.findByEmailValor(usuarioLogin.getEnderecoEmail());
        if(usuarioLogin.senha().confere(usuarioCadastrado.getSenhaHash()))
            return jwtTokenProvider.gerarToken(usuarioCadastrado.getEmail());
        
        throw new CredenciaisInvalidasException("usuario ou senha inválidos.");        
    }    

    @Override
    public String invalidarToken(Token token) {
        if (isTokenInvalidado(token)) throw new IllegalArgumentException("Token inválido.");

        adicionarTokenNaBlackList(token, jwtTokenProvider.getTempoExpiracao(token));
        return "Logout Realizado com sucesso!.";
    }

    private void adicionarTokenNaBlackList(Token token, long tempoExpiracaoMillis) {
        redisTemplate.opsForValue().set(
            token.valor(), "invalidado", tempoExpiracaoMillis, TimeUnit.MILLISECONDS
        );
    }

    private boolean isTokenInvalidado(Token token) {
        return Boolean.TRUE.equals(
            redisTemplate.hasKey(token.valor())
        );
    }
}
