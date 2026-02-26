package com.JhonCodari.GestorFacil.exception;

public class ContaBancariaIntegracaoException extends RuntimeException {

    public ContaBancariaIntegracaoException(String mensagem) {
        super(mensagem);
    }

    public ContaBancariaIntegracaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
