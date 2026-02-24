package com.JhonCodari.GestorFacil.mapper;

import com.JhonCodari.GestorFacil.dto.TransacaoRespostaDTO;
import com.JhonCodari.GestorFacil.model.TransacaoEntity;

public class TransacaoMapper {

    public static TransacaoRespostaDTO toDTO(TransacaoEntity entidade) {
        return new TransacaoRespostaDTO(
            entidade.getId(),
            entidade.getDescricao(),
            entidade.getValor(),
            entidade.getTipo(),
            entidade.getCategoria(),
            entidade.getData(),
            entidade.getCriadoEm(),
            entidade.getAtualizadoEm()
        );
    }
}
