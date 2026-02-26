package com.JhonCodari.GestorFacil.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.JhonCodari.GestorFacil.dto.CambioRespostaDTO;

@Component
public class CambioClient {

    private final RestClient restClient;

    public CambioClient(
        @Qualifier("cambioRestClient")
        RestClient cambioRestClient
    ) {
        this.restClient = cambioRestClient;
    }

    public CambioRespostaDTO consultaCambio(String moeda, String data) {
        try {
            return restClient
                .get()
                .uri("/cotacao/{moeda}/{data}", moeda, data)
                .retrieve()
                .body(CambioRespostaDTO.class);
        } catch (Exception ex) {
            throw new RuntimeException(
                "Nao foi possivel consultar os dados de cambio. Tente novamente mais tarde.", ex);
        }
    }
    
}
