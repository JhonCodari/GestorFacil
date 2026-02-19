package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.JhonCodari.GestorFacil.service.EmailService;

@Service
@ConditionalOnProperty(name = "email.mock", havingValue = "true")
public class EmailServiceMockImpl implements EmailService {

    @Override
    public void enviarEmailRecuperacaoSenha(String destinatario, String token) {
        System.out.println("=== EMAIL MOCK - RECUPERAÇÃO DE SENHA ===");
        System.out.println("Destinatário: " + destinatario);
        System.out.println("Token de recuperação: " + token);
        System.out.println("Link de recuperação: http://localhost:8080/auth/password/reset?token=" + token);
        System.out.println("=========================================");
    }

    @Override
    public void enviarEmailConfirmacao(String destinatario, String token) {
        System.out.println("=== EMAIL MOCK - CONFIRMAÇÃO DE EMAIL ===");
        System.out.println("Destinatário: " + destinatario);
        System.out.println("Token de confirmação: " + token);
        System.out.println("Link de confirmação: http://localhost:8080/auth/confirma-email?token=" + token);
        System.out.println("==========================================");
    }
}
