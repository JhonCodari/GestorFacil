package com.JhonCodari.GestorFacil.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key chaveSecreta = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long tempoExpiracao = 3600000;

    public String gerarToken(String emailUsuario) {

        Date agora = new Date();
        Date dataExpiracao = new Date(agora.getTime() + tempoExpiracao);

        return Jwts.builder()
                .setSubject(emailUsuario)
                .setIssuedAt(agora)
                .setExpiration(dataExpiracao)
                .signWith(chaveSecreta)
                .compact();

       
    }

    public String extrairEmail(String token) {
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
    
}
