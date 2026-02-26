package com.JhonCodari.GestorFacil.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${integracao.banco.url-base}")
    private String urlBaseBanco;

    @Value("${integracao.cambio.url-base}")
    private String urlBaseCambioApi;

    @Bean
    public RestClient contaBancariaRestClient() {
        return RestClient.builder()
            .baseUrl(urlBaseBanco)
            .defaultHeader("Accept", "application/json")
            .build();
    }

    @Bean
    public RestClient cambioRestClient() {
        return RestClient.builder()
            .baseUrl(urlBaseCambioApi)
            .defaultHeader("Accept", "application/json")
            .build();
    }
}
