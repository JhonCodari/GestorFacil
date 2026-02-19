package com.JhonCodari.GestorFacil.service.impl;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.JhonCodari.GestorFacil.exception.ConfirmacaoEmailException;
import com.JhonCodari.GestorFacil.model.TokenConfirmacaoEmailEntity;
import com.JhonCodari.GestorFacil.model.Usuario;
import com.JhonCodari.GestorFacil.model.valueobjects.TokenConfirmacaoEmail;
import com.JhonCodari.GestorFacil.repository.TokenConfirmacaoEmailRepository;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.service.ConfirmacaoEmailService;
import com.JhonCodari.GestorFacil.service.EmailService;

@Service
public class ConfirmacaoEmailServiceImpl implements ConfirmacaoEmailService {

    private final TokenConfirmacaoEmailRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public ConfirmacaoEmailServiceImpl(
            TokenConfirmacaoEmailRepository tokenRepository,
            UsuarioRepository usuarioRepository,
            EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void gerarTokenConfirmacao(Usuario usuario) {
        var token = TokenConfirmacaoEmail.gerar();
        var dataExpiracao = Instant.now().plusSeconds(24 * 60 * 60);
        
        var tokenEntity = new TokenConfirmacaoEmailEntity(token, usuario, dataExpiracao);
        tokenRepository.save(tokenEntity);
        
        emailService.enviarEmailConfirmacao(usuario.getEmail().valor(), token.valor());
    }

    @Override
    @Transactional
    public void confirmarEmail(String tokenValor) {
        var tokenEntity = tokenRepository.findByToken_Valor(tokenValor);
        
        if (tokenEntity == null) throw new ConfirmacaoEmailException("Token de confirmação inválido");        
        if (tokenEntity.isUtilizado()) throw new ConfirmacaoEmailException("Token de confirmação já foi utilizado");        
        if (tokenEntity.isExpirado()) throw new ConfirmacaoEmailException("Token de confirmação expirado");
        
        var usuario = tokenEntity.getUsuario();
        usuario.marcarEmailComoVerificado();
        usuarioRepository.save(usuario);
        
        tokenEntity.marcarComoUtilizado();
        tokenRepository.save(tokenEntity);
    }
}
