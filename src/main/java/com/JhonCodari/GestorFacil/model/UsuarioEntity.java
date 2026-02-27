package com.JhonCodari.GestorFacil.model;

import java.util.ArrayList;
import java.util.List;

import com.JhonCodari.GestorFacil.model.valueobjects.*;
import jakarta.persistence.*;



@Entity
@Table(
    name="usuarios",
    uniqueConstraints = {
        @UniqueConstraint( name = "uk_usuario_email_valor", columnNames = "email_valor")
    }
)
public class UsuarioEntity {

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

    @Column(name = "id_conta_bancaria", nullable = true, length = 50)
    private String idContaBancaria;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshTokenEntity> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransacaoEntity> transacoes = new ArrayList<>();

    protected UsuarioEntity() {}

    public UsuarioEntity(NomeCompleto nomeCompleto, EmailUsuario email, Senha senha) {
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

    public List<TransacaoEntity> getTransacoes() {
        return transacoes;
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

    public String getIdContaBancaria() {
        return idContaBancaria;
    }

    public boolean possuiContaBancariaVinculada() {
        return idContaBancaria != null && !idContaBancaria.isBlank();
    }

    public void vincularContaBancaria(String idContaBancaria) {
        this.idContaBancaria = idContaBancaria;
    }

    public void desvincularContaBancaria() {
        this.idContaBancaria = null;
    }

    public void atualizarNome(NomeCompleto nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public void atualizarEmail(EmailUsuario email) {
        this.email = email;
        this.emailVerificado = false;
    }
    
}
