package com.JhonCodari.GestorFacil.dto;

import java.util.List;

public record ImportacaoResultadoDTO(
    int totalLinhas,
    int sucesso,
    int erros,
    List<String> detalhesErros
) {}
