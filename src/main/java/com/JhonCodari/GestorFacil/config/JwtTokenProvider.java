package com.JhonCodari.GestorFacil.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import com.JhonCodari.GestorFacil.model.valueobjects.*;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key chaveSecreta = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long tempoExpiracao = 900000;

    public String gerarToken(EmailUsuario emailUsuario) {

        Date agora = new Date();
        Date dataExpiracao = new Date(agora.getTime() + tempoExpiracao);

        return Jwts.builder()
                .setSubject(emailUsuario.valor())
                .setIssuedAt(agora)
                .setExpiration(dataExpiracao)
                .signWith(chaveSecreta)
                .compact();

       
    }

    public String extrairEmail(Token token) {
        return Jwts.parserBuilder()
                .setSigningKey(chaveSecreta)
                .build()
                .parseClaimsJws(token.valor())
                .getBody()
                .getSubject();
    }

    public boolean validarToken(Token token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(chaveSecreta)
                .build()
                .parseClaimsJws(token.valor());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
}
