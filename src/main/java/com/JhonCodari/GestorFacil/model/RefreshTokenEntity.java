package com.JhonCodari.GestorFacil.model;

import java.time.Instant;

import com.JhonCodari.GestorFacil.model.valueobjects.RefreshToken;

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
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_valor", nullable = false, length = 512)
    private RefreshToken refreshToken;

    @ManyToOne(fetch = FetchType.LAZY, optional =  false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column (name = "data_expiracao", nullable = false)
    private Instant dataExpiracao;

    @Column(name = "revogado", nullable = false)
    private boolean revogado;

    public RefreshTokenEntity() {}

    public RefreshTokenEntity(
        RefreshToken refreshToken,
        Usuario usuario,
        Instant expiracao
    ) {
        this.refreshToken = refreshToken;
        this.usuario = usuario;
        this.dataExpiracao = expiracao;
        this.revogado = false;
    }

    @PrePersist
    private void onCreate() {
        this.criadoEm =this.criadoEm == null ? Instant.now() : criadoEm;
    }

    public Long getId() {
        return id;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public boolean isRevogado() {
        return revogado;
    }

    public void revogar() {
        this.revogado = true;
    }
    
}
