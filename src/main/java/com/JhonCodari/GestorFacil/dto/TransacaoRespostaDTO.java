package com.JhonCodari.GestorFacil.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;

public record TransacaoRespostaDTO(
    Long id,
    String descricao,
    BigDecimal valor,
    TipoTransacao tipo,
    CategoriaTransacao categoria,
    LocalDate data,
    Instant criadoEm,
    Instant atualizadoEm
) {}
