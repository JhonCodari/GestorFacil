package com.JhonCodari.GestorFacil.service;

import com.JhonCodari.GestorFacil.dto.RefreshTokenRequestDTO;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.model.valueobjects.RefreshToken;

public interface RefreshTokenService {

    public RefreshToken criar(EmailUsuario usuarioEmail);
    public boolean validar(RefreshToken refreshToken);
    public void rotacionar(RefreshTokenRequestDTO refreshTokenRequestDTO);
    public void revogar(RefreshToken refreshToken);    
}
