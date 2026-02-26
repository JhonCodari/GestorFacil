package com.JhonCodari.GestorFacil.dto;

import java.math.BigDecimal;

public record ContaBancariaRespostaDTO(
    String idConta,
    String numeroConta,
    String agencia,
    String banco,
    String tipoConta,
    BigDecimal saldoDisponivel,
    BigDecimal saldoBloqueado,
    BigDecimal saldoTotal,
    BigDecimal limiteChequeEspecial,
    String moeda,
    boolean ativa,
    String atualizadoEm
) {}
