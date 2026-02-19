package com.JhonCodari.GestorFacil.service;

import com.JhonCodari.GestorFacil.model.valueobjects.Senha;

public interface RecuperacaoSenhaService {
    void solicitarRecuperacao(String email);
    void redefinirSenha(String tokenValor, Senha novaSenha);
}
