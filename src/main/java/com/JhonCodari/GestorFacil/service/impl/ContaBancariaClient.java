package com.JhonCodari.GestorFacil.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.JhonCodari.GestorFacil.dto.ContaBancariaDTO;
import com.JhonCodari.GestorFacil.exception.ContaBancariaIntegracaoException;

@Component
public class ContaBancariaClient {

    private final RestClient restClient;

    public ContaBancariaClient(RestClient contaBancariaRestClient) {
        this.restClient = contaBancariaRestClient;
    }

    public ContaBancariaDTO buscarContaPorId(String idConta) {
        try {
            return restClient
                .get()
                .uri("/contas-bancarias/{id}", idConta)
                .retrieve()
                .body(ContaBancariaDTO.class);
        } catch (RestClientException ex) {
            throw new ContaBancariaIntegracaoException(
                "Nao foi possivel consultar os dados da conta bancaria. Tente novamente mais tarde.", ex);
        }
    }
}
