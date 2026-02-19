package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.JhonCodari.GestorFacil.dto.UsuarioCadastroDTO;
import com.JhonCodari.GestorFacil.dto.UsuarioRespostaDTO;
import com.JhonCodari.GestorFacil.exception.EmailJaCadastradoException;
import com.JhonCodari.GestorFacil.service.ConfirmacaoEmailService;
import com.JhonCodari.GestorFacil.service.UsuarioService;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.model.Usuario;
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
            throw new EmailJaCadastradoException("Este E-mail já está em uso.");

        var usuario = new Usuario(
            usuarioCadastroDTO.nomeCompleto(),
            usuarioCadastroDTO.email(),
            usuarioCadastroDTO.senha()
        );
        var usuarioSalvo = this.usuarioRepository.save(usuario);
        
        confirmacaoEmailService.gerarTokenConfirmacao(usuarioSalvo);
        
        return new UsuarioRespostaDTO(
            usuarioSalvo.getId(),
            usuarioSalvo.getNomeCompleto(),
            usuarioSalvo.getEmail()
        );
    }

    @Override
    public Usuario consultarUsuarioPorEmail (EmailUsuario email) {
        if (!this.usuarioRepository.existsByEmailValor(email.valor())) 
            throw new EmailJaCadastradoException("Usuário não encontrado.");
        return this.usuarioRepository.findByEmailValor(email.valor());        
    }
    
}
