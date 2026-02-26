package com.JhonCodari.GestorFacil.dto;

import jakarta.validation.constraints.NotBlank;

public record ContaBancariaVinculoDTO(
    @NotBlank(message = "O ID da conta bancaria nao pode ser vazio") 
    String idConta
) {}
