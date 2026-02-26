package com.JhonCodari.GestorFacil.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.JhonCodari.GestorFacil.dto.CotacaoDTO;
import com.JhonCodari.GestorFacil.integration.CambioClient;
import com.JhonCodari.GestorFacil.service.CambioConversorService;

@Service
public class CambioConversorServiceImpl implements CambioConversorService {

    private static final String FECHAMENTO_PTAX = "FECHAMENTO PTAX";

    private final CambioClient cambioClient;

    public CambioConversorServiceImpl(CambioClient cambioClient) {
        this.cambioClient = cambioClient;
    }

    @Override
    public BigDecimal buscarTaxaFechamentoPTAX(String moeda) {
        String dataOntem = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        var cotacaoResposta = cambioClient.consultaCambio(moeda, dataOntem);

        return cotacaoResposta.cotacoes().stream()
            .filter(c -> FECHAMENTO_PTAX.equals(c.tipoBoletim()))
            .findFirst()
            .map(CotacaoDTO::cotacaoCompra)
            .map(BigDecimal::valueOf)
            .orElseThrow(() -> new RuntimeException(
                "Cotacao FECHAMENTO PTAX nao encontrada para a moeda: " + moeda));
    }

    @Override
    public BigDecimal converter(BigDecimal valorEmBRL, BigDecimal taxaCotacao) {
        if (valorEmBRL == null) {
            return null;
        }
        return valorEmBRL.divide(taxaCotacao, 2, RoundingMode.HALF_UP);
    }
}
