package com.JhonCodari.GestorFacil.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${integracao.banco.url-base}")
    private String urlBaseBanco;

    @Bean
    public RestClient contaBancariaRestClient() {
        return RestClient.builder()
            .baseUrl(urlBaseBanco)
            .defaultHeader("Accept", "application/json")
            .build();
    }
}
