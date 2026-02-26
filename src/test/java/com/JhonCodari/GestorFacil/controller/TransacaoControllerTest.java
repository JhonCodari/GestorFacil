package com.JhonCodari.GestorFacil.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.JhonCodari.GestorFacil.dto.TransacaoRespostaDTO;
import com.JhonCodari.GestorFacil.exception.GlobalExceptionHandler;
import com.JhonCodari.GestorFacil.exception.TransacaoNaoEncontradaException;
import com.JhonCodari.GestorFacil.exception.TransacaoNaoPertenceAoUsuarioException;
import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;
import com.JhonCodari.GestorFacil.service.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransacaoControllerTest {

    @Mock
    private TransacaoService transacaoService;

    @InjectMocks
    private TransacaoController controller;

    private MockMvc mockMvc;
    private UsernamePasswordAuthenticationToken principal;
    private TransacaoRespostaDTO respostaDTO;

    @BeforeEach
    void configurar() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        principal = new UsernamePasswordAuthenticationToken("joao@email.com", null);

        respostaDTO = new TransacaoRespostaDTO(
            1L,
            "Salario",
            new BigDecimal("5000.00"),
            TipoTransacao.CREDITO,
            CategoriaTransacao.DEPOSITO,
            LocalDate.of(2026, 2, 1),
            Instant.now(),
            Instant.now()
        );
    }

    @Test
    void deveCriarTransacaoComPayloadValido() throws Exception {
        when(transacaoService.criar(any(), eq("joao@email.com"))).thenReturn(respostaDTO);

        var payload = """
            {
                "descricao": "Salario",
                "valor": 5000.00,
                "tipo": "CREDITO",
                "categoria": "DEPOSITO",
                "data": "2026-02-01"
            }
            """;

        mockMvc.perform(post("/transacoes")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.descricao").value("Salario"))
            .andExpect(jsonPath("$.valor").value(5000.00))
            .andExpect(jsonPath("$.tipo").value("CREDITO"))
            .andExpect(jsonPath("$.categoria").value("DEPOSITO"));
    }

    @Test
    void deveRetornar400ComPayloadInvalido() throws Exception {
        var payload = """
            {
                "descricao": "",
                "valor": -10,
                "tipo": null
            }
            """;

        mockMvc.perform(post("/transacoes")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarTransacoesDoUsuario() {
        var pagina = new PageImpl<>(List.of(respostaDTO));
        when(transacaoService.listar(eq("joao@email.com"), any(Pageable.class))).thenReturn(pagina);

        var resultado = controller.listar(PageRequest.of(0, 10), principal);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(1, resultado.getBody().getTotalElements());
        assertEquals("Salario", resultado.getBody().getContent().getFirst().descricao());
    }

    @Test
    void deveBuscarTransacaoPorId() throws Exception {
        when(transacaoService.buscarPorId(eq(1L), eq("joao@email.com"))).thenReturn(respostaDTO);

        mockMvc.perform(get("/transacoes/1")
                .principal(principal))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deveRetornar404ParaIdInexistente() throws Exception {
        when(transacaoService.buscarPorId(eq(99L), eq("joao@email.com")))
            .thenThrow(new TransacaoNaoEncontradaException("Transacao com ID 99 nao encontrada."));

        mockMvc.perform(get("/transacoes/99")
                .principal(principal))
            .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar403ParaTransacaoDeOutroUsuario() throws Exception {
        when(transacaoService.buscarPorId(eq(1L), eq("joao@email.com")))
            .thenThrow(new TransacaoNaoPertenceAoUsuarioException("Transacao nao pertence ao usuario autenticado."));

        mockMvc.perform(get("/transacoes/1")
                .principal(principal))
            .andExpect(status().isForbidden());
    }

    @Test
    void deveAtualizarTransacaoComSucesso() throws Exception {
        var respostaAtualizada = new TransacaoRespostaDTO(
            1L,
            "Salario atualizado",
            new BigDecimal("6000.00"),
            TipoTransacao.CREDITO,
            CategoriaTransacao.DEPOSITO,
            LocalDate.of(2026, 2, 1),
            Instant.now(),
            Instant.now()
        );

        when(transacaoService.atualizar(eq(1L), any(), eq("joao@email.com"))).thenReturn(respostaAtualizada);

        var payload = """
            {
                "valor": 15000.90,
                "descricao": "credito de salario mensal em conta",
                "categoria": "SALARIO",
                "data": "2024-06-24",
                "tipo": "CREDITO"
            }
            """;

        mockMvc.perform(put("/transacoes/1")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.descricao").value("Salario atualizado"))
            .andExpect(jsonPath("$.valor").value(6000.00));
    }

    @Test
    void deveDeletarTransacaoComSucesso() throws Exception {
        doNothing().when(transacaoService).deletar(eq(1L), eq("joao@email.com"));

        mockMvc.perform(delete("/transacoes/1")
                .principal(principal))
            .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar403AoDeletarTransacaoDeOutroUsuario() throws Exception {
        doThrow(new TransacaoNaoPertenceAoUsuarioException("Transacao nao pertence ao usuario autenticado."))
            .when(transacaoService).deletar(eq(1L), eq("joao@email.com"));

        mockMvc.perform(delete("/transacoes/1")
                .principal(principal))
            .andExpect(status().isForbidden());
    }
}
