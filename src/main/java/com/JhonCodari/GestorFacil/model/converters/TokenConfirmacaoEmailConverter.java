package com.JhonCodari.GestorFacil.model.converters;

import com.JhonCodari.GestorFacil.model.valueobjects.TokenConfirmacaoEmail;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TokenConfirmacaoEmailConverter implements AttributeConverter<TokenConfirmacaoEmail, String> {

    @Override
    public String convertToDatabaseColumn(TokenConfirmacaoEmail token) {
        return token != null ? token.valor() : null;
    }

    @Override
    public TokenConfirmacaoEmail convertToEntityAttribute(String dbData) {
        return dbData != null ? new TokenConfirmacaoEmail(dbData) : null;
    }
}
