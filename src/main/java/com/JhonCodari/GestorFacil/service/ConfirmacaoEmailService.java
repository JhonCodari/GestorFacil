package com.JhonCodari.GestorFacil.service;

import com.JhonCodari.GestorFacil.model.UsuarioEntity;

public interface ConfirmacaoEmailService {
    void gerarTokenConfirmacao(UsuarioEntity usuario);
    void confirmarEmail(String tokenValor);
}
