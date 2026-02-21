package com.JhonCodari.GestorFacil.service.impl;

import com.JhonCodari.GestorFacil.exception.ConfirmacaoEmailException;
import com.JhonCodari.GestorFacil.model.TokenConfirmacaoEmailEntity;
import com.JhonCodari.GestorFacil.model.UsuarioEntity;
import com.JhonCodari.GestorFacil.model.valueobjects.*;
import com.JhonCodari.GestorFacil.repository.TokenConfirmacaoEmailRepository;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmacaoEmailServiceImplTest {

    @Mock
    private TokenConfirmacaoEmailRepository tokenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ConfirmacaoEmailServiceImpl confirmacaoEmailService;

    private UsuarioEntity usuario;
    private final String uuidToken = "550e8400-e29b-41d4-a716-446655440000";

    @BeforeEach
    void configurar() {
        usuario = new UsuarioEntity(
            new NomeCompleto("Joao", "Silva"),
            new EmailUsuario("joao@email.com"),
            new Senha("Senha@123")
        );
    }

    @Test
    void deveGerarTokenConfirmacaoEEnviarEmail() {
        confirmacaoEmailService.gerarTokenConfirmacao(usuario);

        verify(tokenRepository, times(1)).save(any(TokenConfirmacaoEmailEntity.class));
        verify(emailService, times(1)).enviarEmailConfirmacao(eq("joao@email.com"), anyString());
    }

    @Test
    void deveConfirmarEmailComTokenValido() {
        var tokenEntity = new TokenConfirmacaoEmailEntity(
            new Token(uuidToken),
            usuario,
            Instant.now().plusSeconds(3600)
        );
        when(tokenRepository.findByToken_Valor(uuidToken)).thenReturn(tokenEntity);

        confirmacaoEmailService.confirmarEmail(uuidToken);

        assertTrue(usuario.isEmailVerificado());
        assertTrue(tokenEntity.isUtilizado());
        verify(usuarioRepository, times(1)).save(usuario);
        verify(tokenRepository, times(1)).save(tokenEntity);
    }

    @Test
    void deveLancarExcecaoQuandoTokenNaoEncontrado() {
        when(tokenRepository.findByToken_Valor(uuidToken)).thenReturn(null);

        assertThrows(ConfirmacaoEmailException.class, () ->
            confirmacaoEmailService.confirmarEmail(uuidToken)
        );
    }

    @Test
    void deveLancarExcecaoQuandoTokenJaUtilizado() {
        var tokenEntity = new TokenConfirmacaoEmailEntity(
            new Token(uuidToken),
            usuario,
            Instant.now().plusSeconds(3600)
        );
        tokenEntity.marcarComoUtilizado();
        when(tokenRepository.findByToken_Valor(uuidToken)).thenReturn(tokenEntity);

        assertThrows(ConfirmacaoEmailException.class, () ->
            confirmacaoEmailService.confirmarEmail(uuidToken)
        );
    }

    @Test
    void deveLancarExcecaoQuandoTokenExpirado() {
        var tokenEntity = new TokenConfirmacaoEmailEntity(
            new Token(uuidToken),
            usuario,
            Instant.now().minusSeconds(1)
        );
        when(tokenRepository.findByToken_Valor(uuidToken)).thenReturn(tokenEntity);

        assertThrows(ConfirmacaoEmailException.class, () ->
            confirmacaoEmailService.confirmarEmail(uuidToken)
        );
    }
}
