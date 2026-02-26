package com.JhonCodari.GestorFacil.dto;

import java.math.BigDecimal;

import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;

public record AnaliseCategoriaDTO(
    CategoriaTransacao categoria,
    TipoTransacao tipo,
    BigDecimal total,
    BigDecimal percentualDoTotal,
    int quantidade
) {}
