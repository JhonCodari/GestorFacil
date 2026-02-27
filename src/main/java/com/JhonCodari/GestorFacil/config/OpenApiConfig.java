package com.JhonCodari.GestorFacil.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("GestorFacil API")
                .version("1.0.0")
                .description("API de gestao financeira para clientes bancarios. "
                    + "Permite gerenciamento de usuarios, transacoes, analise de despesas, "
                    + "importacao via Excel, autenticacao JWT e integracao com APIs externas.")
                .contact(new Contact()
                    .name("Jhon Codari")
                    .url("https://github.com/JhonCodari")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Informe o token JWT obtido no endpoint /auth/login")));
    }
}
