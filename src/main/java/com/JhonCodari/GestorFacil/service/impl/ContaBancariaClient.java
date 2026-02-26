package com.JhonCodari.GestorFacil.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
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

    public List<ContaBancariaDTO> buscarContasPorUsuarioId(String usuarioId) {
        try {
            return restClient
                .get()
                .uri("/contas-bancarias?usuarioId={usuarioId}", usuarioId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ContaBancariaDTO>>() {});
        } catch (RestClientException ex) {
            throw new ContaBancariaIntegracaoException(
                "Nao foi possivel consultar os dados da conta bancaria. Tente novamente mais tarde.", ex);
        }
    }
}
