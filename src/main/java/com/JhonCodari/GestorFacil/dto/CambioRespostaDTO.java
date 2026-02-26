package com.JhonCodari.GestorFacil.dto;

import java.util.List;

public record CambioRespostaDTO(
    List<CotacaoDTO> cotacoes,
    String moeda,
    String data
) {}
