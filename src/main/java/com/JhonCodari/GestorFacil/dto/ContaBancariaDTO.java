package com.JhonCodari.GestorFacil.dto;

import java.math.BigDecimal;

public record ContaBancariaDTO(
    String id,
    String usuarioId,
    String numeroConta,
    String agencia,
    String banco,
    String codigoBanco,
    String tipoConta,
    BigDecimal saldoDisponivel,
    BigDecimal saldoBloqueado,
    BigDecimal saldoTotal,
    BigDecimal limiteChequeEspecial,
    String moeda,
    String dataAbertura,
    boolean ativa,
    String atualizadoEm
) {}
