package com.JhonCodari.GestorFacil.controller;

import com.JhonCodari.GestorFacil.model.valueobjects.*;
import com.JhonCodari.GestorFacil.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoControllerTest {

    @Mock
    private AutenticacaoService autenticacaoService;

    @Mock
    private AccessTokenService accessTokenService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private ConfirmacaoEmailService confirmacaoEmailService;

    @Mock
    private RecuperacaoSenhaService recuperacaoSenhaService;

    @InjectMocks
    private AutenticacaoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deveRealizarLoginComSucesso() throws Exception {
        when(autenticacaoService.autenticar(any())).thenReturn("access.token.valor");
        when(refreshTokenService.criar(any())).thenReturn(new RefreshToken("refresh.token.valor"));

        var payload = """
            {
                "email": {"valor": "joao@email.com"},
                "senha": {"valor": "Senha@123"}
            }
            """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access.token.valor"))
            .andExpect(jsonPath("$.refreshToken").value("refresh.token.valor"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void deveRealizarLogoutComSucesso() throws Exception {
        when(autenticacaoService.invalidarToken(any())).thenReturn("Logout Realizado com sucesso!.");

        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer header.payload.signature"))
            .andExpect(status().isOk())
            .andExpect(content().string("Logout Realizado com sucesso!."));
    }

    @Test
    void deveRealizarRefreshTokenComSucesso() throws Exception {
        var novoRefreshToken = new RefreshToken("novo.refresh.token");
        var novoAccessToken = new AccessToken("novo.access.token");

        when(refreshTokenService.rotacionar(any())).thenReturn(novoRefreshToken);
        when(accessTokenService.renovar(any())).thenReturn(novoAccessToken);

        mockMvc.perform(post("/auth/refresh-token")
                .header("X-Refresh-Token", "header.payload.signature"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("novo.access.token"))
            .andExpect(jsonPath("$.refreshToken").value("novo.refresh.token"));
    }

    @Test
    void deveConfirmarEmailComTokenValido() throws Exception {
        doNothing().when(confirmacaoEmailService).confirmarEmail(anyString());

        mockMvc.perform(get("/auth/confirma-email")
                .param("token", "550e8400-e29b-41d4-a716-446655440000"))
            .andExpect(status().isOk())
            .andExpect(content().string("Email confirmado com sucesso!"));
    }

    @Test
    void deveSolicitarRecuperacaoSenha() throws Exception {
        doNothing().when(recuperacaoSenhaService).solicitarRecuperacao(anyString());

        var payload = """
            {"email": "joao@email.com"}
            """;

        mockMvc.perform(post("/auth/senha/esqueci")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(content().string("Email de recuperação enviado com sucesso!"));
    }

    @Test
    void deveRedefinirSenhaComTokenValido() throws Exception {
        doNothing().when(recuperacaoSenhaService).redefinirSenha(anyString(), any());

        var payload = """
            {"novaSenha": {"valor": "NovaSenha@456"}}
            """;

        mockMvc.perform(post("/auth/senha/reset")
                .param("token", "550e8400-e29b-41d4-a716-446655440000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk());
    }
}


@WebMvcTest(value = AutenticacaoController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutenticacaoService autenticacaoService;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private ConfirmacaoEmailService confirmacaoEmailService;

    @MockitoBean
    private RecuperacaoSenhaService recuperacaoSenhaService;

    @Test
    void deveRealizarLoginComSucesso() throws Exception {
        when(autenticacaoService.autenticar(any())).thenReturn("access.token.valor");
        when(refreshTokenService.criar(any())).thenReturn(new RefreshToken("refresh.token.valor"));

        var payload = """
            {
                "email": {"valor": "joao@email.com"},
                "senha": {"valor": "Senha@123"}
            }
            """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access.token.valor"))
            .andExpect(jsonPath("$.refreshToken").value("refresh.token.valor"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void deveRealizarLogoutComSucesso() throws Exception {
        when(autenticacaoService.invalidarToken(any())).thenReturn("Logout Realizado com sucesso!.");

        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer header.payload.signature"))
            .andExpect(status().isOk())
            .andExpect(content().string("Logout Realizado com sucesso!."));
    }

    @Test
    void deveRealizarRefreshTokenComSucesso() throws Exception {
        var novoRefreshToken = new RefreshToken("novo.refresh.token");
        var novoAccessToken = new AccessToken("novo.access.token");

        when(refreshTokenService.rotacionar(any())).thenReturn(novoRefreshToken);
        when(accessTokenService.renovar(any())).thenReturn(novoAccessToken);

        mockMvc.perform(post("/auth/refresh-token")
                .header("X-Refresh-Token", "header.payload.signature"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("novo.access.token"))
            .andExpect(jsonPath("$.refreshToken").value("novo.refresh.token"));
    }

    @Test
    void deveConfirmarEmailComTokenValido() throws Exception {
        doNothing().when(confirmacaoEmailService).confirmarEmail(anyString());

        mockMvc.perform(get("/auth/confirma-email")
                .param("token", "550e8400-e29b-41d4-a716-446655440000"))
            .andExpect(status().isOk())
            .andExpect(content().string("Email confirmado com sucesso!"));
    }

    @Test
    void deveSolicitarRecuperacaoSenha() throws Exception {
        doNothing().when(recuperacaoSenhaService).solicitarRecuperacao(anyString());

        var payload = """
            {"email": "joao@email.com"}
            """;

        mockMvc.perform(post("/auth/senha/esqueci")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(content().string("Email de recuperação enviado com sucesso!"));
    }

    @Test
    void deveRedefinirSenhaComTokenValido() throws Exception {
        doNothing().when(recuperacaoSenhaService).redefinirSenha(anyString(), any());

        var payload = """
            {"novaSenha": {"valor": "NovaSenha@456"}}
            """;

        mockMvc.perform(post("/auth/senha/reset")
                .param("token", "550e8400-e29b-41d4-a716-446655440000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk());
    }
}


@WebMvcTest(value = AutenticacaoController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutenticacaoService autenticacaoService;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private ConfirmacaoEmailService confirmacaoEmailService;

    @MockitoBean
    private RecuperacaoSenhaService recuperacaoSenhaService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void deveRealizarLoginComSucesso() throws Exception {
        when(autenticacaoService.autenticar(any())).thenReturn("access.token.valor");
        when(refreshTokenService.criar(any())).thenReturn(new RefreshToken("refresh.token.valor"));

        var payload = """
            {
                "email": {"valor": "joao@email.com"},
                "senha": {"valor": "Senha@123"}
            }
            """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access.token.valor"))
            .andExpect(jsonPath("$.refreshToken").value("refresh.token.valor"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @WithMockUser
    void deveRealizarLogoutComSucesso() throws Exception {
        when(autenticacaoService.invalidarToken(any())).thenReturn("Logout Realizado com sucesso!.");

        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer header.payload.signature")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string("Logout Realizado com sucesso!."));
    }

    @Test
    @WithMockUser
    void deveRealizarRefreshTokenComSucesso() throws Exception {
        var novoRefreshToken = new RefreshToken("novo.refresh.token");
        var novoAccessToken = new AccessToken("novo.access.token");

        when(refreshTokenService.rotacionar(any())).thenReturn(novoRefreshToken);
        when(accessTokenService.renovar(any())).thenReturn(novoAccessToken);

        mockMvc.perform(post("/auth/refresh-token")
                .header("X-Refresh-Token", "header.payload.signature")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("novo.access.token"))
            .andExpect(jsonPath("$.refreshToken").value("novo.refresh.token"));
    }

    @Test
    void deveConfirmarEmailComTokenValido() throws Exception {
        doNothing().when(confirmacaoEmailService).confirmarEmail(anyString());

        mockMvc.perform(get("/auth/confirma-email")
                .param("token", "550e8400-e29b-41d4-a716-446655440000"))
            .andExpect(status().isOk())
            .andExpect(content().string("Email confirmado com sucesso!"));
    }

    @Test
    void deveSolicitarRecuperacaoSenha() throws Exception {
        doNothing().when(recuperacaoSenhaService).solicitarRecuperacao(anyString());

        var payload = """
            {"email": "joao@email.com"}
            """;

        mockMvc.perform(post("/auth/senha/esqueci")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string("Email de recuperação enviado com sucesso!"));
    }

    @Test
    void deveRedefinirSenhaComTokenValido() throws Exception {
        doNothing().when(recuperacaoSenhaService).redefinirSenha(anyString(), any());

        var payload = """
            {"novaSenha": {"valor": "NovaSenha@456"}}
            """;

        mockMvc.perform(post("/auth/senha/reset")
                .param("token", "550e8400-e29b-41d4-a716-446655440000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .with(csrf()))
            .andExpect(status().isOk());
    }
}
