package com.JhonCodari.GestorFacil.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;

import jakarta.persistence.*;

@Entity
@Table(name = "transacoes")
public class TransacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "valor", nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoTransacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaTransacao categoria;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected TransacaoEntity() {}

    public TransacaoEntity(
        String descricao,
        BigDecimal valor,
        TipoTransacao tipo,
        CategoriaTransacao categoria,
        LocalDate data,
        UsuarioEntity usuario
    ) {
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.data = data;
        this.usuario = usuario;
    }

    @PrePersist
    protected void aocriar() {
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.atualizadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public CategoriaTransacao getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaTransacao categoria) {
        this.categoria = categoria;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}
