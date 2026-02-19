package com.JhonCodari.GestorFacil.service.impl;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.JhonCodari.GestorFacil.exception.ConfirmacaoEmailException;
import com.JhonCodari.GestorFacil.model.TokenRecuperacaoSenhaEntity;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.model.valueobjects.Senha;
import com.JhonCodari.GestorFacil.model.valueobjects.TokenRecuperacaoSenha;
import com.JhonCodari.GestorFacil.repository.TokenRecuperacaoSenhaRepository;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.service.EmailService;
import com.JhonCodari.GestorFacil.service.RecuperacaoSenhaService;
import com.JhonCodari.GestorFacil.service.UsuarioService;

@Service
public class RecuperacaoSenhaServiceImpl implements RecuperacaoSenhaService {

    private final TokenRecuperacaoSenhaRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final EmailService emailService;

    public RecuperacaoSenhaServiceImpl(
            TokenRecuperacaoSenhaRepository tokenRepository,
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService,
            EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void solicitarRecuperacao(String email) {
        var emailUsuario = new EmailUsuario(email);
        var usuario = usuarioService.consultarUsuarioPorEmail(emailUsuario);
        
        var token = TokenRecuperacaoSenha.gerar();
        var dataExpiracao = Instant.now().plusSeconds(60 * 60);
        
        var tokenEntity = new TokenRecuperacaoSenhaEntity(token, usuario, dataExpiracao);
        tokenRepository.save(tokenEntity);
        
        emailService.enviarEmailRecuperacaoSenha(email, token.valor());
    }

    @Override
    @Transactional
    public void redefinirSenha(String tokenValor, Senha novaSenha) {
        var tokenEntity = tokenRepository.findByToken_Valor(tokenValor);
        
        if (tokenEntity == null) throw new ConfirmacaoEmailException("Token de recuperação inválido");        
        if (tokenEntity.isUtilizado()) throw new ConfirmacaoEmailException("Token de recuperação já foi utilizado");
        if (tokenEntity.isExpirado()) throw new ConfirmacaoEmailException("Token de recuperação expirado");
        
        var usuario = tokenEntity.getUsuario();
        usuario.atualizarSenha(novaSenha);
        usuarioRepository.save(usuario);
        
        tokenEntity.marcarComoUtilizado();
        tokenRepository.save(tokenEntity);
    }
}
