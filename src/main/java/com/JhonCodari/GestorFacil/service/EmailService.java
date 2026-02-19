package com.JhonCodari.GestorFacil.service;

public interface EmailService {
    void enviarEmailRecuperacaoSenha(String destinatario, String token);
    void enviarEmailConfirmacao(String destinatario, String token);
}
