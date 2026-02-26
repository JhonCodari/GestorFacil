package com.JhonCodari.GestorFacil.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AnaliseFinanceiraRespostaDTO(
    BigDecimal totalReceitas,
    BigDecimal totalDespesas,
    BigDecimal totalTransferencias,
    BigDecimal saldo,
    BigDecimal saldoContaBancaria,
    int quantidadeTransacoes,
    LocalDate periodoInicio,
    LocalDate periodoFim,
    List<AnaliseCategoriaDTO> porCategoria
) {}
