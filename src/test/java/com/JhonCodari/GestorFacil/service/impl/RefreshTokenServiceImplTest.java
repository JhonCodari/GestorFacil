package com.JhonCodari.GestorFacil.service.impl;

import com.JhonCodari.GestorFacil.config.JwtTokenProvider;
import com.JhonCodari.GestorFacil.exception.RefreshTokenNaoEncontradoException;
import com.JhonCodari.GestorFacil.exception.RefreshTokenRevogadoException;
import com.JhonCodari.GestorFacil.model.RefreshTokenEntity;
import com.JhonCodari.GestorFacil.model.Usuario;
import com.JhonCodari.GestorFacil.model.valueobjects.*;
import com.JhonCodari.GestorFacil.repository.RefreshTokenRepository;
import com.JhonCodari.GestorFacil.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private Usuario usuario;
    private RefreshToken refreshToken;
    private RefreshTokenEntity entidadeAtiva;
    private final String tokenValor = "header.payload.signature";

    @BeforeEach
    void configurar() {
        usuario = new Usuario(
            new NomeCompleto("Joao", "Silva"),
            new EmailUsuario("joao@email.com"),
            new Senha("Senha@123")
        );
        refreshToken = new RefreshToken(tokenValor);
        entidadeAtiva = new RefreshTokenEntity(refreshToken, usuario, Instant.now().plusSeconds(3600));
    }

    @Test
    void deveCriarRefreshTokenComSucesso() {
        var email = new EmailUsuario("joao@email.com");
        when(usuarioService.consultarUsuarioPorEmail(email)).thenReturn(usuario);
        when(jwtTokenProvider.gerarRefreshToken(email)).thenReturn(tokenValor);
        when(jwtTokenProvider.getDataExpiracao(tokenValor)).thenReturn(Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.save(any())).thenReturn(entidadeAtiva);

        var resultado = refreshTokenService.criar(email);

        assertNotNull(resultado);
        assertEquals(tokenValor, resultado.valor());
    }

    @Test
    void deveValidarRefreshTokenAtivo() {
        when(jwtTokenProvider.validarToken(tokenValor)).thenReturn(true);
        when(refreshTokenRepository.findByRefreshToken_valor(tokenValor)).thenReturn(entidadeAtiva);
        when(jwtTokenProvider.extrairSubject(tokenValor)).thenReturn("joao@email.com");
        when(usuarioService.consultarUsuarioPorEmail(any())).thenReturn(usuario);

        assertTrue(refreshTokenService.validar(refreshToken));
    }

    @Test
    void deveRetornarFalsoQuandoTokenJwtInvalido() {
        when(jwtTokenProvider.validarToken(tokenValor)).thenReturn(false);

        assertFalse(refreshTokenService.validar(refreshToken));
    }

    @Test
    void deveLancarExcecaoQuandoTokenRevogadoNaValidacao() {
        entidadeAtiva.revogar();
        when(jwtTokenProvider.validarToken(tokenValor)).thenReturn(true);
        when(refreshTokenRepository.findByRefreshToken_valor(tokenValor)).thenReturn(entidadeAtiva);

        assertThrows(RefreshTokenRevogadoException.class, () ->
            refreshTokenService.validar(refreshToken)
        );
    }

    @Test
    void deveLancarExcecaoQuandoTokenNaoEncontradoNaValidacao() {
        when(jwtTokenProvider.validarToken(tokenValor)).thenReturn(true);
        when(refreshTokenRepository.findByRefreshToken_valor(tokenValor)).thenReturn(null);

        assertThrows(RefreshTokenNaoEncontradoException.class, () ->
            refreshTokenService.validar(refreshToken)
        );
    }

    @Test
    void deveRotacionarTokenComSucesso() {
        var novoTokenValor = "novo.header.payload";
        var email = new EmailUsuario("joao@email.com");
        var novoToken = new RefreshToken(novoTokenValor);
        var novaEntidade = new RefreshTokenEntity(novoToken, usuario, Instant.now().plusSeconds(3600));

        when(jwtTokenProvider.validarToken(tokenValor)).thenReturn(true);
        when(refreshTokenRepository.findByRefreshToken_valor(tokenValor)).thenReturn(entidadeAtiva);
        when(jwtTokenProvider.extrairSubject(tokenValor)).thenReturn("joao@email.com");
        when(usuarioService.consultarUsuarioPorEmail(any())).thenReturn(usuario);
        when(refreshTokenRepository.save(entidadeAtiva)).thenReturn(entidadeAtiva);
        when(jwtTokenProvider.gerarRefreshToken(any())).thenReturn(novoTokenValor);
        when(jwtTokenProvider.getDataExpiracao(novoTokenValor)).thenReturn(Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.save(argThat(e -> e != entidadeAtiva))).thenReturn(novaEntidade);

        var resultado = refreshTokenService.rotacionar(refreshToken);

        assertNotNull(resultado);
        assertEquals(novoTokenValor, resultado.valor());
        assertTrue(entidadeAtiva.isRevogado());
    }

    @Test
    void deveRevogarTokenComSucesso() {
        when(refreshTokenRepository.findByRefreshToken_valor(tokenValor)).thenReturn(entidadeAtiva);

        refreshTokenService.revogar(refreshToken);

        assertTrue(entidadeAtiva.isRevogado());
        verify(refreshTokenRepository, times(1)).save(entidadeAtiva);
    }

    @Test
    void deveLancarExcecaoAoRevogarTokenNaoEncontrado() {
        when(refreshTokenRepository.findByRefreshToken_valor(tokenValor)).thenReturn(null);

        assertThrows(RefreshTokenNaoEncontradoException.class, () ->
            refreshTokenService.revogar(refreshToken)
        );
    }

    @Test
    void deveRevogarTodosOsTokensDoUsuario() {
        var entidade2 = new RefreshTokenEntity(
            new RefreshToken("outro.token.valor"),
            usuario,
            Instant.now().plusSeconds(3600)
        );
        when(refreshTokenRepository.findAllByUsuario_Email_Valor("joao@email.com"))
            .thenReturn(List.of(entidadeAtiva, entidade2));

        refreshTokenService.revogarTodosRefreshTokensDoUsuario("joao@email.com");

        assertTrue(entidadeAtiva.isRevogado());
        assertTrue(entidade2.isRevogado());
        verify(refreshTokenRepository, times(2)).save(any());
    }

    @Test
    void deveIgnorarTokensJaRevogadosAoRevogarTodos() {
        entidadeAtiva.revogar();
        when(refreshTokenRepository.findAllByUsuario_Email_Valor("joao@email.com"))
            .thenReturn(List.of(entidadeAtiva));

        refreshTokenService.revogarTodosRefreshTokensDoUsuario("joao@email.com");

        verify(refreshTokenRepository, never()).save(any());
    }
}
