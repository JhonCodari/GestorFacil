package com.JhonCodari.GestorFacil.service;

import com.JhonCodari.GestorFacil.model.valueobjects.AccessToken;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.model.valueobjects.RefreshToken;

public interface AccessTokenService {

    AccessToken criar(EmailUsuario usuarioEmail);
    
    boolean validar(AccessToken accessToken);
    
    AccessToken renovar(RefreshToken refreshToken);
    
    void revogar(AccessToken accessToken);
    
}
