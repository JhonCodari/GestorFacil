package com.JhonCodari.GestorFacil.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;

import jakarta.validation.constraints.*;

public record TransacaoCadastroDTO(
     
    String descricao,

    @NotNull 
    @Positive 
    BigDecimal valor,

    @NotNull 
    TipoTransacao tipo,

    @NotNull 
    CategoriaTransacao categoria,

    @NotNull 
    LocalDate data
) {}
