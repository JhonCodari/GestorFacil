package com.JhonCodari.GestorFacil.service.impl;

import com.JhonCodari.GestorFacil.dto.UsuarioAtualizacaoDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioCadastroDTO;
import com.JhonCodari.GestorFacil.exception.EmailJaCadastradoException;
import com.JhonCodari.GestorFacil.exception.UsuarioNaoEncontradoException;
import com.JhonCodari.GestorFacil.model.UsuarioEntity;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.model.valueobjects.NomeCompleto;
import com.JhonCodari.GestorFacil.model.valueobjects.Senha;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.service.ConfirmacaoEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConfirmacaoEmailService confirmacaoEmailService;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioCadastroDTO dadosCadastro;
    private UsuarioEntity usuarioSalvo;

    @BeforeEach
    void configurar() {
        var nomeCompleto = new NomeCompleto("Joao", "Silva");
        var email = new EmailUsuario("joao@email.com");
        var senha = new Senha("Senha@123");
        dadosCadastro = new UsuarioCadastroDTO(nomeCompleto, email, senha);
        usuarioSalvo = new UsuarioEntity(nomeCompleto, email, senha);
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {
        when(usuarioRepository.existsByEmailValor("joao@email.com")).thenReturn(false);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioSalvo);

        var resultado = usuarioService.cadastrarUsuario(dadosCadastro);

        assertNotNull(resultado);
        assertEquals("joao@email.com", resultado.email().valor());
        verify(confirmacaoEmailService, times(1)).gerarTokenConfirmacao(any(UsuarioEntity.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaEstaCadastrado() {
        when(usuarioRepository.existsByEmailValor("joao@email.com")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () ->
            usuarioService.cadastrarUsuario(dadosCadastro)
        );

        verify(usuarioRepository, never()).save(any());
        verify(confirmacaoEmailService, never()).gerarTokenConfirmacao(any());
    }

    @Test
    void deveRetornarUsuarioQuandoEmailExiste() {
        when(usuarioRepository.findByEmailValor("joao@email.com")).thenReturn(usuarioSalvo);

        var resultado = usuarioService.consultarUsuarioPorEmail(new EmailUsuario("joao@email.com"));

        assertNotNull(resultado);
        assertEquals("joao@email.com", resultado.getEnderecoEmail());
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoEncontrado() {
        when(usuarioRepository.findByEmailValor("inexistente@email.com")).thenReturn(null);

        assertThrows(UsuarioNaoEncontradoException.class, () ->
            usuarioService.consultarUsuarioPorEmail(new EmailUsuario("inexistente@email.com"))
        );
    }

    @Test
    void deveAtualizarNomeDoUsuario() {
        var novoNome = new NomeCompleto("Maria", "Santos");
        var dados = new UsuarioAtualizacaoDTO(novoNome, null);

        when(usuarioRepository.findByEmailValor("joao@email.com")).thenReturn(usuarioSalvo);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioSalvo);

        var resultado = usuarioService.atualizarUsuario(new EmailUsuario("joao@email.com"), dados);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));
        verify(confirmacaoEmailService, never()).gerarTokenConfirmacao(any());
    }

    @Test
    void deveAtualizarEmailDoUsuarioEReenviarConfirmacao() {
        var novoEmail = new EmailUsuario("novo@email.com");
        var dados = new UsuarioAtualizacaoDTO(null, novoEmail);

        when(usuarioRepository.findByEmailValor("joao@email.com")).thenReturn(usuarioSalvo);
        when(usuarioRepository.existsByEmailValor("novo@email.com")).thenReturn(false);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioSalvo);

        var resultado = usuarioService.atualizarUsuario(new EmailUsuario("joao@email.com"), dados);

        assertNotNull(resultado);
        verify(confirmacaoEmailService, times(1)).gerarTokenConfirmacao(any(UsuarioEntity.class));
    }

    @Test
    void deveLancarExcecaoQuandoAtualizarParaEmailJaExistente() {
        var emailExistente = new EmailUsuario("existente@email.com");
        var dados = new UsuarioAtualizacaoDTO(null, emailExistente);

        when(usuarioRepository.findByEmailValor("joao@email.com")).thenReturn(usuarioSalvo);
        when(usuarioRepository.existsByEmailValor("existente@email.com")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () ->
            usuarioService.atualizarUsuario(new EmailUsuario("joao@email.com"), dados)
        );
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveExcluirUsuarioComSucesso() {
        when(usuarioRepository.findByEmailValor("joao@email.com")).thenReturn(usuarioSalvo);

        usuarioService.excluirUsuario(new EmailUsuario("joao@email.com"));

        verify(usuarioRepository, times(1)).delete(usuarioSalvo);
    }

    @Test
    void deveLancarExcecaoAoExcluirUsuarioInexistente() {
        when(usuarioRepository.findByEmailValor("naoexiste@email.com")).thenReturn(null);

        assertThrows(UsuarioNaoEncontradoException.class, () ->
            usuarioService.excluirUsuario(new EmailUsuario("naoexiste@email.com"))
        );
        verify(usuarioRepository, never()).delete(any());
    }
}
