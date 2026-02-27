package com.JhonCodari.GestorFacil.service.impl;

import com.JhonCodari.GestorFacil.config.JwtTokenProvider;
import com.JhonCodari.GestorFacil.dto.UsuarioLoginDTO;
import com.JhonCodari.GestorFacil.exception.CredenciaisInvalidasException;
import com.JhonCodari.GestorFacil.exception.EmailNaoVerificadoException;
import com.JhonCodari.GestorFacil.model.UsuarioEntity;
import com.JhonCodari.GestorFacil.model.valueobjects.*;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.service.AccessTokenService;
import com.JhonCodari.GestorFacil.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AccessTokenService accessTokenService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AutenticacaoServiceImpl autenticacaoService;

    private UsuarioEntity usuarioVerificado;
    private UsuarioLoginDTO loginDTO;
    private final String senhaValor = "Senha@123";

    @BeforeEach
    void configurar() {
        var nomeCompleto = new NomeCompleto("Joao", "Silva");
        var email = new EmailUsuario("joao@email.com");
        var senha = new Senha(senhaValor);
        usuarioVerificado = new UsuarioEntity(nomeCompleto, email, senha);
        usuarioVerificado.marcarEmailComoVerificado();

        loginDTO = new UsuarioLoginDTO(email, senha);
    }

    @Test
    void deveAutenticarUsuarioComSucesso() {
        when(usuarioRepository.findByEmailValor("joao@email.com")).thenReturn(usuarioVerificado);
        when(accessTokenService.criar(any(EmailUsuario.class)))
            .thenReturn(new AccessToken("header.payload.signature"));

        var accessToken = autenticacaoService.autenticar(loginDTO);

        assertNotNull(accessToken);
        assertEquals("header.payload.signature", accessToken);
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoVerificado() {
        var nomeCompleto = new NomeCompleto("Joao", "Silva");
        var email = new EmailUsuario("joao@email.com");
        var senha = new Senha(senhaValor);
        var usuarioNaoVerificado = new UsuarioEntity(nomeCompleto, email, senha);

        when(usuarioRepository.findByEmailValor("joao@email.com")).thenReturn(usuarioNaoVerificado);

        assertThrows(EmailNaoVerificadoException.class, () ->
            autenticacaoService.autenticar(loginDTO)
        );
    }

    @Test
    void deveLancarExcecaoQuandoCredenciaisInvalidas() {
        var nomeCompleto = new NomeCompleto("Joao", "Silva");
        var email = new EmailUsuario("joao@email.com");
        var senhaCorreta = new Senha("SenhaCorreta@1");
        var usuarioComSenhaCorreta = new UsuarioEntity(nomeCompleto, email, senhaCorreta);
        usuarioComSenhaCorreta.marcarEmailComoVerificado();

        when(usuarioRepository.findByEmailValor("joao@email.com")).thenReturn(usuarioComSenhaCorreta);

        assertThrows(CredenciaisInvalidasException.class, () ->
            autenticacaoService.autenticar(loginDTO)
        );
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoCadastrado() {
        when(usuarioRepository.findByEmailValor("joao@email.com")).thenReturn(null);

        assertThrows(CredenciaisInvalidasException.class, () ->
            autenticacaoService.autenticar(loginDTO)
        );
    }

    @Test
    void deveInvalidarTokenNoLogout() {
        var tokenJwt = "header.payload.signature";
        var token = new Token("Bearer " + tokenJwt);
        var accessToken = new AccessToken(tokenJwt);

        when(jwtTokenProvider.extrairSubject(tokenJwt)).thenReturn("joao@email.com");

        var resultado = autenticacaoService.invalidarToken(token);

        assertEquals("Logout Realizado com sucesso!.", resultado);
        verify(accessTokenService, times(1)).revogar(any(AccessToken.class));
        verify(refreshTokenService, times(1)).revogarTodosRefreshTokensDoUsuario("joao@email.com");
    }
}
