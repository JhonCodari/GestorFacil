package com.JhonCodari.GestorFacil.model;

import java.time.Instant;

import com.JhonCodari.GestorFacil.model.valueobjects.Token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "tokens_recuperacao_senha")
public class TokenRecuperacaoSenhaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_valor", nullable = false, unique = true)
    private Token token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "data_expiracao", nullable = false)
    private Instant dataExpiracao;

    @Column(name = "utilizado", nullable = false)
    private boolean utilizado;

    public TokenRecuperacaoSenhaEntity() {}

    public TokenRecuperacaoSenhaEntity(
        Token token,
        UsuarioEntity usuario,
        Instant dataExpiracao
    ) {
        this.token = token;
        this.usuario = usuario;
        this.dataExpiracao = dataExpiracao;
        this.utilizado = false;
    }

    @PrePersist
    private void onCreate() {
        this.criadoEm = this.criadoEm == null ? Instant.now() : criadoEm;
    }

    public Long getId() {
        return id;
    }

    public Token getToken() {
        return token;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getDataExpiracao() {
        return dataExpiracao;
    }

    public boolean isUtilizado() {
        return utilizado;
    }

    public void marcarComoUtilizado() {
        this.utilizado = true;
    }

    public boolean isExpirado() {
        return Instant.now().isAfter(dataExpiracao);
    }
}
