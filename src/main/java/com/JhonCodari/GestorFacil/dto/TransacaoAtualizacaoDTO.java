package com.JhonCodari.GestorFacil.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TransacaoAtualizacaoDTO(
    String descricao,

    @Positive 
    BigDecimal valor,

    @NotBlank(message = "O tipo da transação não pode ser vazio ou nulo.")
    TipoTransacao tipo,

    @NotBlank(message = "A categoria da transação não pode ser vazia ou nula.")
    CategoriaTransacao categoria,
    
    LocalDate data
) {}
