package com.JhonCodari.GestorFacil.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransacaoCadastroDTO(
    @NotBlank String descricao,
    @NotNull @Positive BigDecimal valor,
    @NotNull TipoTransacao tipo,
    @NotNull CategoriaTransacao categoria,
    @NotNull LocalDate data
) {}
