package com.JhonCodari.GestorFacil.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransacaoAtualizacaoDTO(
    String descricao,

    @Positive 
    BigDecimal valor,

    @NotNull(message = "O tipo da transação não pode ser nulo.")
    TipoTransacao tipo,

    @NotNull(message = "A categoria da transação não pode ser nula.")
    CategoriaTransacao categoria,
    
    LocalDate data
) {}
