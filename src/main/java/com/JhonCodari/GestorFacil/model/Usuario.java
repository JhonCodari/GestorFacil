package com.JhonCodari.GestorFacil.model;

import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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

    protected Usuario() {}

    public Usuario(NomeCompleto nomeCompleto, EmailUsuario email, String senhaHash) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senhaHash = senhaHash;
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
    
    public String getSenhaHash() {
        return senhaHash;
    }
    
}
