package com.JhonCodari.GestorFacil.model.converters;

import com.JhonCodari.GestorFacil.model.valueobjects.Token;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TokenConfirmacaoEmailConverter implements AttributeConverter<Token, String> {

    @Override
    public String convertToDatabaseColumn(Token token) {
        return token != null ? token.valor() : null;
    }

    @Override
    public Token convertToEntityAttribute(String dbData) {
        return dbData != null ? new Token(dbData) : null;
    }
}
