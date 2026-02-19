package com.JhonCodari.GestorFacil.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.JhonCodari.GestorFacil.model.valueobjects.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.time.Instant;

@Component
public class JwtTokenProvider {

    private final Key chaveSecreta;
    private final long tempoExpiracaoAccessToken;
    private final long tempoExpiracaoRefreshToken;
    
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token.expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token.expiration}") long refreshTokenExpiration) {
        this.chaveSecreta = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.tempoExpiracaoAccessToken = accessTokenExpiration;
        this.tempoExpiracaoRefreshToken = refreshTokenExpiration;
    }

    public String gerarAccessToken(EmailUsuario emailUsuario) {
        return gerarToken(emailUsuario.valor(), tempoExpiracaoAccessToken);              
    }

    public String gerarRefreshToken(EmailUsuario emailUsuario) {
        return gerarToken(emailUsuario.valor(), tempoExpiracaoRefreshToken);              
    }

    public String gerarTokenPersonalizado(String subject, Date dataExpiracao) {
        return gerarTokenComExpiracao(subject, dataExpiracao);
    }

    public String extrairSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(chaveSecreta)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(chaveSecreta)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getDataCriacao(String token) {
        var claims = Jwts.parserBuilder()
            .setSigningKey(chaveSecreta)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getIssuedAt().getTime();
    }

    public Instant getDataCriacaoToInstant(String token) {
        var claims = Jwts.parserBuilder()
            .setSigningKey(chaveSecreta)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getIssuedAt().toInstant();
    }

    public Instant getDataExpiracao(String token) {
        return getDataCriacaoToInstant(token)
        .plusMillis(getTempoExpiracao(token));       
    }

    public long getTempoExpiracao(String token) {
        Date dataExpiracao = Jwts.parserBuilder()
                .setSigningKey(chaveSecreta)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        Date agora = new Date();
        return dataExpiracao.getTime() - agora.getTime();
    }

    public Key getChaveSecreta() {
        return chaveSecreta;
    }

    private String gerarToken(String subject, long tempoExpiracao) {
        var agora = new Date();
        var dataExpiracao = new Date(agora.getTime() + tempoExpiracao);
        return gerarTokenComExpiracao(subject, dataExpiracao);    
    }    

    private String gerarTokenComExpiracao(String subject, Date dataExpiracao) {
        Date agora = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(agora)
                .setExpiration(dataExpiracao)
                .signWith(chaveSecreta)
                .compact(); 
    }  
    
}
