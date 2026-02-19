package com.JhonCodari.GestorFacil.service.impl;

import com.JhonCodari.GestorFacil.config.JwtTokenProvider;
import com.JhonCodari.GestorFacil.model.valueobjects.*;
import com.JhonCodari.GestorFacil.service.RefreshTokenService;
import com.JhonCodari.GestorFacil.service.UsuarioService;
import com.JhonCodari.GestorFacil.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessTokenServiceImplTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AccessTokenServiceImpl accessTokenService;

    private final String tokenValor = "header.payload.signature";
    private Usuario usuario;

    @BeforeEach
    void configurar() {
        usuario = new Usuario(
            new NomeCompleto("Joao", "Silva"),
            new EmailUsuario("joao@email.com"),
            new Senha("Senha@123")
        );
    }

    @Test
    void deveCriarAccessTokenComSucesso() {
        var email = new EmailUsuario("joao@email.com");
        when(usuarioService.consultarUsuarioPorEmail(email)).thenReturn(usuario);
        when(jwtTokenProvider.gerarAccessToken(email)).thenReturn(tokenValor);

        var resultado = accessTokenService.criar(email);

        assertNotNull(resultado);
        assertEquals(tokenValor, resultado.valor());
    }

    @Test
    void deveValidarTokenAtivoComSucesso() {
        var accessToken = new AccessToken(tokenValor);
        when(redisTemplate.hasKey(tokenValor)).thenReturn(false);
        when(jwtTokenProvider.validarToken(tokenValor)).thenReturn(true);
        when(jwtTokenProvider.extrairSubject(tokenValor)).thenReturn("joao@email.com");
        when(usuarioService.consultarUsuarioPorEmail(any())).thenReturn(usuario);

        assertTrue(accessTokenService.validar(accessToken));
    }

    @Test
    void deveRetornarFalsoParaTokenRevogado() {
        var accessToken = new AccessToken(tokenValor);
        when(redisTemplate.hasKey(tokenValor)).thenReturn(true);

        assertFalse(accessTokenService.validar(accessToken));
    }

    @Test
    void deveRetornarFalsoParaTokenJwtInvalido() {
        var accessToken = new AccessToken(tokenValor);
        when(redisTemplate.hasKey(tokenValor)).thenReturn(false);
        when(jwtTokenProvider.validarToken(tokenValor)).thenReturn(false);

        assertFalse(accessTokenService.validar(accessToken));
    }

    @Test
    void deveRenovarAccessTokenAPartirDeRefreshTokenValido() {
        var refreshToken = new RefreshToken(tokenValor);
        var novoTokenValor = "novo.header.payload";
        when(refreshTokenService.validar(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.extrairSubject(tokenValor)).thenReturn("joao@email.com");
        when(usuarioService.consultarUsuarioPorEmail(any())).thenReturn(usuario);
        when(jwtTokenProvider.gerarAccessToken(any())).thenReturn(novoTokenValor);

        var resultado = accessTokenService.renovar(refreshToken);

        assertNotNull(resultado);
        assertEquals(novoTokenValor, resultado.valor());
    }

    @Test
    void deveLancarExcecaoAoRenovarComRefreshTokenInvalido() {
        var refreshToken = new RefreshToken(tokenValor);
        when(refreshTokenService.validar(refreshToken)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
            accessTokenService.renovar(refreshToken)
        );
    }

    @Test
    void deveRevogarAccessTokenAdicionandoNaBlacklist() {
        var accessToken = new AccessToken(tokenValor);
        when(jwtTokenProvider.getTempoExpiracao(tokenValor)).thenReturn(900000L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        accessTokenService.revogar(accessToken);

        verify(valueOperations, times(1)).set(eq(tokenValor), eq("revogado"), eq(900000L), any());
    }
}
