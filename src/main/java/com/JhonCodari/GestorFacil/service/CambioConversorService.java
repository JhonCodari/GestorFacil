package com.JhonCodari.GestorFacil.service;

import java.math.BigDecimal;

public interface CambioConversorService {

    BigDecimal buscarTaxaFechamentoPTAX(String moeda);

    BigDecimal converter(BigDecimal valorEmBRL, BigDecimal taxaCotacao);
}
