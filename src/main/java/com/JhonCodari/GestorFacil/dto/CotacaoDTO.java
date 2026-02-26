package com.JhonCodari.GestorFacil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CotacaoDTO(
    @JsonProperty("paridade_compra") 
    double paridadeCompra,

    @JsonProperty("paridade_venda") 
    double paridadeVenda,

    @JsonProperty("cotacao_compra") 
    double cotacaoCompra,

    @JsonProperty("cotacao_venda") 
    double cotacaoVenda,

    @JsonProperty("data_hora_cotacao") 
    String dataHoraCotacao,

    @JsonProperty("tipo_boletim") 
    String tipoBoletim
) {}
