package com.JhonCodari.GestorFacil.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.JhonCodari.GestorFacil.dto.TransacaoAtualizacaoDTO;
import com.JhonCodari.GestorFacil.dto.TransacaoCadastroDTO;
import com.JhonCodari.GestorFacil.exception.TransacaoNaoEncontradaException;
import com.JhonCodari.GestorFacil.exception.TransacaoNaoPertenceAoUsuarioException;
import com.JhonCodari.GestorFacil.model.TransacaoEntity;
import com.JhonCodari.GestorFacil.model.UsuarioEntity;
import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.model.valueobjects.NomeCompleto;
import com.JhonCodari.GestorFacil.model.valueobjects.Senha;
import com.JhonCodari.GestorFacil.repository.TransacaoRepository;
import com.JhonCodari.GestorFacil.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceImplTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private TransacaoServiceImpl transacaoService;

    private UsuarioEntity usuario;
    private TransacaoEntity transacao;
    private TransacaoCadastroDTO dadosCadastro;

    @BeforeEach
    void configurar() {
        usuario = new UsuarioEntity(
            new NomeCompleto("Joao", "Silva"),
            new EmailUsuario("joao@email.com"),
            new Senha("Senha@123")
        );

        transacao = new TransacaoEntity(
            "Salario",
            new BigDecimal("5000.00"),
            TipoTransacao.CREDITO,
            CategoriaTransacao.DEPOSITO,
            LocalDate.of(2026, 2, 1),
            usuario
        );

        dadosCadastro = new TransacaoCadastroDTO(
            "Salario",
            new BigDecimal("5000.00"),
            TipoTransacao.CREDITO,
            CategoriaTransacao.DEPOSITO,
            LocalDate.of(2026, 2, 1)
        );
    }

    @Test
    void deveCriarTransacaoAssociadaAoUsuarioCorreto() {
        when(usuarioService.consultarUsuarioPorEmail(any(EmailUsuario.class))).thenReturn(usuario);
        when(transacaoRepository.save(any(TransacaoEntity.class))).thenReturn(transacao);

        var resultado = transacaoService.criar(dadosCadastro, "joao@email.com");

        assertNotNull(resultado);
        assertEquals("Salario", resultado.descricao());
        assertEquals(new BigDecimal("5000.00"), resultado.valor());
        assertEquals(TipoTransacao.CREDITO, resultado.tipo());
        assertEquals(CategoriaTransacao.DEPOSITO, resultado.categoria());
        verify(transacaoRepository, times(1)).save(any(TransacaoEntity.class));
    }

    @Test
    void deveListarApenasTransacoesDoUsuarioAutenticado() {
        var pageable = PageRequest.of(0, 10);
        var pagina = new PageImpl<>(List.of(transacao));

        when(usuarioService.consultarUsuarioPorEmail(any(EmailUsuario.class))).thenReturn(usuario);
        when(transacaoRepository.findAllByUsuarioId(eq(usuario.getId()), eq(pageable))).thenReturn(pagina);

        var resultado = transacaoService.listar("joao@email.com", pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Salario", resultado.getContent().getFirst().descricao());
    }

    @Test
    void deveBuscarTransacaoPorIdComSucesso() {
        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));

        var resultado = transacaoService.buscarPorId(1L, "joao@email.com");

        assertNotNull(resultado);
        assertEquals("Salario", resultado.descricao());
    }

    @Test
    void deveLancarTransacaoNaoEncontradaException() {
        when(transacaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TransacaoNaoEncontradaException.class, () ->
            transacaoService.buscarPorId(99L, "joao@email.com")
        );
    }

    @Test
    void deveLancarTransacaoNaoPertenceAoUsuarioException() {
        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));

        assertThrows(TransacaoNaoPertenceAoUsuarioException.class, () ->
            transacaoService.buscarPorId(1L, "outro@email.com")
        );
    }

    @Test
    void deveAtualizarTransacaoComSucesso() {
        var dadosAtualizacao = new TransacaoAtualizacaoDTO(
            "Salario atualizado",
            new BigDecimal("6000.00"),
            null,
            null,
            null
        );

        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));
        when(transacaoRepository.save(any(TransacaoEntity.class))).thenReturn(transacao);

        var resultado = transacaoService.atualizar(1L, dadosAtualizacao, "joao@email.com");

        assertNotNull(resultado);
        verify(transacaoRepository, times(1)).save(any(TransacaoEntity.class));
    }

    @Test
    void deveAtualizarParcialmenteApenasCamposInformados() {
        var dadosAtualizacao = new TransacaoAtualizacaoDTO(
            null,
            new BigDecimal("7500.00"),
            null,
            null,
            null
        );

        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));
        when(transacaoRepository.save(any(TransacaoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        transacaoService.atualizar(1L, dadosAtualizacao, "joao@email.com");

        assertEquals("Salario", transacao.getDescricao());
        assertEquals(new BigDecimal("7500.00"), transacao.getValor());
    }

    @Test
    void deveDeletarTransacaoComSucesso() {
        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));

        transacaoService.deletar(1L, "joao@email.com");

        verify(transacaoRepository, times(1)).delete(transacao);
    }

    @Test
    void naoDeveDeletarTransacaoDeOutroUsuario() {
        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));

        assertThrows(TransacaoNaoPertenceAoUsuarioException.class, () ->
            transacaoService.deletar(1L, "outro@email.com")
        );

        verify(transacaoRepository, never()).delete(any());
    }
}
