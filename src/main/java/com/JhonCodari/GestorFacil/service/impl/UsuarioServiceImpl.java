package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.JhonCodari.GestorFacil.dto.UsuarioAtualizacaoDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioCadastroDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;
import com.JhonCodari.GestorFacil.exception.EmailJaCadastradoException;
import com.JhonCodari.GestorFacil.exception.UsuarioNaoEncontradoException;
import com.JhonCodari.GestorFacil.service.ConfirmacaoEmailService;
import com.JhonCodari.GestorFacil.service.UsuarioService;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.mapper.UsuarioMapper;
import com.JhonCodari.GestorFacil.model.UsuarioEntity;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ConfirmacaoEmailService confirmacaoEmailService;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            ConfirmacaoEmailService confirmacaoEmailService) {
        this.usuarioRepository = usuarioRepository;
        this.confirmacaoEmailService = confirmacaoEmailService;
    }

    @Override
    @Transactional
    public UsuarioRespostaDTO cadastrarUsuario(UsuarioCadastroDTO usuarioCadastroDTO) {

        if (this.usuarioRepository.existsByEmailValor(usuarioCadastroDTO.emailValor())) 
            throw new EmailJaCadastradoException("Este E-mail ja esta em uso.");

        var usuario = new UsuarioEntity(
            usuarioCadastroDTO.nomeCompleto(),
            usuarioCadastroDTO.email(),
            usuarioCadastroDTO.senha()
        );
        var usuarioSalvo = this.usuarioRepository.save(usuario);
        
        confirmacaoEmailService.gerarTokenConfirmacao(usuarioSalvo);
        
        return UsuarioMapper.toDTO(usuarioSalvo);
    }

    @Override
    public UsuarioEntity consultarUsuarioPorEmail(EmailUsuario email) {
        var usuario = this.usuarioRepository.findByEmailValor(email.valor());
        if (usuario == null)
            throw new UsuarioNaoEncontradoException("Usuario nao encontrado.");
        return usuario;
    }

    @Override
    @Transactional
    public UsuarioRespostaDTO atualizarUsuario(EmailUsuario emailAtual, UsuarioAtualizacaoDTO dados) {
        var usuario = consultarUsuarioPorEmail(emailAtual);

        if (dados.nomeCompleto() != null)
            usuario.atualizarNome(dados.nomeCompleto());

        if (dados.email() != null && !dados.email().valor().equals(emailAtual.valor())) {
            if (this.usuarioRepository.existsByEmailValor(dados.email().valor()))
                throw new EmailJaCadastradoException("Este E-mail ja esta em uso.");
            usuario.atualizarEmail(dados.email());
            confirmacaoEmailService.gerarTokenConfirmacao(usuario);
        }

        var usuarioAtualizado = this.usuarioRepository.save(usuario);
        return UsuarioMapper.toDTO(usuarioAtualizado);
    }

    @Override
    @Transactional
    public void excluirUsuario(EmailUsuario email) {
        var usuario = consultarUsuarioPorEmail(email);
        this.usuarioRepository.delete(usuario);
    }
}
