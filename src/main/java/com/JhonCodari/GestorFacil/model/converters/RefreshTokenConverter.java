package com.JhonCodari.GestorFacil.model.converters;

import com.JhonCodari.GestorFacil.model.valueobjects.RefreshToken;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RefreshTokenConverter implements AttributeConverter<RefreshToken, String> {

    @Override
    public String convertToDatabaseColumn(RefreshToken refreshToken) {
        return refreshToken ==  null ? null : refreshToken.valor();
    }

    @Override
    public RefreshToken convertToEntityAttribute(String valorDoBancoDeDados) {
        return valorDoBancoDeDados == null ? null : new RefreshToken(valorDoBancoDeDados);
    }    
}
