package com.JhonCodari.GestorFacil.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;

import jakarta.validation.constraints.Positive;

public record TransacaoAtualizacaoDTO(
    String descricao,

    @Positive 
    BigDecimal valor,

    TipoTransacao tipo,

    CategoriaTransacao categoria,
    
    LocalDate data
) {}
