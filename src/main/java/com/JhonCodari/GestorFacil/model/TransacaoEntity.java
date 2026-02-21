package com.JhonCodari.GestorFacil.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;

import jakarta.persistence.*;

@Entity
@Table(
    name = "transacoes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_transacao_id", columnNames = "id")
    }
)
public class TransacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao", nullable = true, length = 200)
    private String descricao;
    
    @Column(name = "valor", nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;
    
    @Column(name = "tipo", nullable = false)
    private TipoTransacao tipo;
    
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

    public TransacaoEntity() {}

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

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public CategoriaTransacao getCategoria() {
        return categoria;
    }

    public LocalDate getData() {
        return data;
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
