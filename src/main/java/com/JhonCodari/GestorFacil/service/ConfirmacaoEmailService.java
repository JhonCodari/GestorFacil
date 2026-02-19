package com.JhonCodari.GestorFacil.service;

import com.JhonCodari.GestorFacil.model.Usuario;

public interface ConfirmacaoEmailService {
    void gerarTokenConfirmacao(Usuario usuario);
    void confirmarEmail(String tokenValor);
}
