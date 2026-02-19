package com.JhonCodari.GestorFacil.service.impl;

import com.JhonCodari.GestorFacil.exception.ConfirmacaoEmailException;
import com.JhonCodari.GestorFacil.model.TokenRecuperacaoSenhaEntity;
import com.JhonCodari.GestorFacil.model.Usuario;
import com.JhonCodari.GestorFacil.model.valueobjects.*;
import com.JhonCodari.GestorFacil.repository.TokenRecuperacaoSenhaRepository;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.service.EmailService;
import com.JhonCodari.GestorFacil.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecuperacaoSenhaServiceImplTest {

    @Mock
    private TokenRecuperacaoSenhaRepository tokenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RecuperacaoSenhaServiceImpl recuperacaoSenhaService;

    private Usuario usuario;
    private final String uuidToken = "550e8400-e29b-41d4-a716-446655440002";

    @BeforeEach
    void configurar() {
        usuario = new Usuario(
            new NomeCompleto("Joao", "Silva"),
            new EmailUsuario("joao@email.com"),
            new Senha("Senha@123")
        );
    }

    @Test
    void deveSolicitarRecuperacaoEEnviarEmail() {
        when(usuarioService.consultarUsuarioPorEmail(any())).thenReturn(usuario);

        recuperacaoSenhaService.solicitarRecuperacao("joao@email.com");

        verify(tokenRepository, times(1)).save(any(TokenRecuperacaoSenhaEntity.class));
        verify(emailService, times(1)).enviarEmailRecuperacaoSenha(eq("joao@email.com"), anyString());
    }

    @Test
    void deveRedefinirSenhaComTokenValido() {
        var tokenEntity = new TokenRecuperacaoSenhaEntity(
            new TokenRecuperacaoSenha(uuidToken),
            usuario,
            Instant.now().plusSeconds(3600)
        );
        var novaSenha = new Senha("NovaSenha@456");
        when(tokenRepository.findByToken_Valor(uuidToken)).thenReturn(tokenEntity);

        recuperacaoSenhaService.redefinirSenha(uuidToken, novaSenha);

        assertTrue(tokenEntity.isUtilizado());
        verify(usuarioRepository, times(1)).save(usuario);
        verify(tokenRepository, times(1)).save(tokenEntity);
    }

    @Test
    void deveLancarExcecaoQuandoTokenNaoEncontrado() {
        when(tokenRepository.findByToken_Valor(uuidToken)).thenReturn(null);

        assertThrows(ConfirmacaoEmailException.class, () ->
            recuperacaoSenhaService.redefinirSenha(uuidToken, new Senha("NovaSenha@456"))
        );
    }

    @Test
    void deveLancarExcecaoQuandoTokenJaUtilizado() {
        var tokenEntity = new TokenRecuperacaoSenhaEntity(
            new TokenRecuperacaoSenha(uuidToken),
            usuario,
            Instant.now().plusSeconds(3600)
        );
        tokenEntity.marcarComoUtilizado();
        when(tokenRepository.findByToken_Valor(uuidToken)).thenReturn(tokenEntity);

        assertThrows(ConfirmacaoEmailException.class, () ->
            recuperacaoSenhaService.redefinirSenha(uuidToken, new Senha("NovaSenha@456"))
        );
    }

    @Test
    void deveLancarExcecaoQuandoTokenExpirado() {
        var tokenEntity = new TokenRecuperacaoSenhaEntity(
            new TokenRecuperacaoSenha(uuidToken),
            usuario,
            Instant.now().minusSeconds(1)
        );
        when(tokenRepository.findByToken_Valor(uuidToken)).thenReturn(tokenEntity);

        assertThrows(ConfirmacaoEmailException.class, () ->
            recuperacaoSenhaService.redefinirSenha(uuidToken, new Senha("NovaSenha@456"))
        );
    }
}
