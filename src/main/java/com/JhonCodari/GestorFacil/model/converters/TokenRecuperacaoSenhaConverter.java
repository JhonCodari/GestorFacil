package com.JhonCodari.GestorFacil.model.converters;

import com.JhonCodari.GestorFacil.model.valueobjects.TokenRecuperacaoSenha;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TokenRecuperacaoSenhaConverter implements AttributeConverter<TokenRecuperacaoSenha, String> {

    @Override
    public String convertToDatabaseColumn(TokenRecuperacaoSenha token) {
        return token != null ? token.valor() : null;
    }

    @Override
    public TokenRecuperacaoSenha convertToEntityAttribute(String dbData) {
        return dbData != null ? new TokenRecuperacaoSenha(dbData) : null;
    }
}
