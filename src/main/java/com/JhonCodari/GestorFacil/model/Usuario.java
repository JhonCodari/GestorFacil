package com.JhonCodari.GestorFacil.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import com.JhonCodari.GestorFacil.model.valueobjects.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(
    name="usuarios",
    uniqueConstraints = {
        @UniqueConstraint( name = "uk_usuario_email_valor", columnNames = "email_valor")
    }
)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "primeiroNome", column = @Column(name = "primeiro_nome", nullable = false, length = 50)),
        @AttributeOverride(name = "sobrenome", column = @Column(name = "sobrenome", nullable = false, length = 50))
    })
    private NomeCompleto nomeCompleto;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "valor", column = @Column(name = "email_valor", nullable = false, length = 100))
    })
    private EmailUsuario email;

    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Column(name = "email_verificado", nullable = false)
    private boolean emailVerificado;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshTokenEntity> refreshTokens = new ArrayList<>();

    protected Usuario() {}

    public Usuario(NomeCompleto nomeCompleto, EmailUsuario email, Senha senha) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senhaHash = senha.hash();
        this.emailVerificado = false;
    }

    public Long getId() {
        return id;
    }

    public NomeCompleto getNomeCompleto() {
        return nomeCompleto;
    }

    public EmailUsuario getEmail() {
        return email;
    }

    public String getEnderecoEmail() {
        return email.valor();
    }
    
    public String getSenhaHash() {
        return senhaHash;
    }

    public boolean isEmailVerificado() {
        return emailVerificado;
    }

    public void marcarEmailComoVerificado() {
        this.emailVerificado = true;
    }

    public void atualizarSenha(Senha novaSenha) {
        this.senhaHash = novaSenha.hash();
    }
    
}
