package com.JhonCodari.GestorFacil.controller;

import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;
import com.JhonCodari.GestorFacil.model.Usuario;
import com.JhonCodari.GestorFacil.model.valueobjects.*;
import com.JhonCodari.GestorFacil.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deveCadastrarUsuarioComSucesso() throws Exception {
        var nomeCompleto = new NomeCompleto("Joao", "Silva");
        var email = new EmailUsuario("joao@email.com");
        var resposta = new UsuarioRespostaDTO(1L, nomeCompleto, email);

        when(usuarioService.cadastrarUsuario(any())).thenReturn(resposta);

        var payload = """
            {
                "nomeCompleto": {
                    "primeiroNome": "Joao",
                    "sobrenome": "Silva"
                },
                "email": {"valor": "joao@email.com"},
                "senha": {"valor": "Senha@123"}
            }
            """;

        mockMvc.perform(post("/usuario/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email.valor").value("joao@email.com"));
    }

    @Test
    void deveRetornarPerfilDoUsuarioAutenticado() throws Exception {
        var nomeCompleto = new NomeCompleto("Joao", "Silva");
        var email = new EmailUsuario("joao@email.com");
        var senha = new Senha("Senha@123");
        var usuario = new Usuario(nomeCompleto, email, senha);
        var principal = new UsernamePasswordAuthenticationToken("joao@email.com", null);

        when(usuarioService.consultarUsuarioPorEmail(any())).thenReturn(usuario);

        mockMvc.perform(get("/usuario/perfil").principal(principal))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email.valor").value("joao@email.com"));
    }
}


@WebMvcTest(value = UsuarioController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void deveCadastrarUsuarioComSucesso() throws Exception {
        var nomeCompleto = new NomeCompleto("Joao", "Silva");
        var email = new EmailUsuario("joao@email.com");
        var resposta = new UsuarioRespostaDTO(1L, nomeCompleto, email);

        when(usuarioService.cadastrarUsuario(any())).thenReturn(resposta);

        var payload = """
            {
                "nomeCompleto": {
                    "primeiroNome": "Joao",
                    "sobrenome": "Silva"
                },
                "email": {"valor": "joao@email.com"},
                "senha": {"valor": "Senha@123"}
            }
            """;

        mockMvc.perform(post("/usuario/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email.valor").value("joao@email.com"));
    }

    @Test
    void deveRetornarPerfilDoUsuarioAutenticado() throws Exception {
        var nomeCompleto = new NomeCompleto("Joao", "Silva");
        var email = new EmailUsuario("joao@email.com");
        var senha = new Senha("Senha@123");
        var usuario = new Usuario(nomeCompleto, email, senha);
        var principal = new UsernamePasswordAuthenticationToken("joao@email.com", null);

        when(usuarioService.consultarUsuarioPorEmail(any())).thenReturn(usuario);

        mockMvc.perform(get("/usuario/perfil").principal(principal))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email.valor").value("joao@email.com"));
    }
}


@WebMvcTest(value = UsuarioController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void deveCadastrarUsuarioComSucesso() throws Exception {
        var nomeCompleto = new NomeCompleto("Joao", "Silva");
        var email = new EmailUsuario("joao@email.com");
        var resposta = new UsuarioRespostaDTO(1L, nomeCompleto, email);

        when(usuarioService.cadastrarUsuario(any())).thenReturn(resposta);

        var payload = """
            {
                "nomeCompleto": {
                    "primeiroNome": "Joao",
                    "sobrenome": "Silva"
                },
                "email": {"valor": "joao@email.com"},
                "senha": {"valor": "Senha@123"}
            }
            """;

        mockMvc.perform(post("/usuario/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email.valor").value("joao@email.com"));
    }

    @Test
    @WithMockUser(username = "joao@email.com")
    void deveRetornarPerfilDoUsuarioAutenticado() throws Exception {
        var nomeCompleto = new NomeCompleto("Joao", "Silva");
        var email = new EmailUsuario("joao@email.com");
        var senha = new Senha("Senha@123");
        var usuario = new Usuario(nomeCompleto, email, senha);

        when(usuarioService.consultarUsuarioPorEmail(any())).thenReturn(usuario);

        mockMvc.perform(get("/usuario/perfil"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email.valor").value("joao@email.com"));
    }

    @Test
    void deveRetornar401AoAcessarPerfilSemAutenticacao() throws Exception {
        mockMvc.perform(get("/usuario/perfil"))
            .andExpect(status().isUnauthorized());
    }
}
