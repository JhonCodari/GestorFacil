package com.JhonCodari.GestorFacil.model.valueobjects;

import java.time.Instant;

import jakarta.validation.Valid;

// essa classe deve centrar as informações sobre o usuario que serão usadas em todo o siatema, ela será o modelo de usuario.

public record Usuario(

    // @Valid
    // NomeCompleto nomeCompleto,

    // @Valid
    // EmailUsuario email,

    //Instant datadeNascimento, vai para o objeto que altera os dados de perfil do usuario "UsuarioPerfilDTO"

    //@Valid
    //Cpf cpf, vai para o objeto que altera os dados de perfil do usuario "UsuarioPerfilDTO"

    // @Valid
    // StatusUsuarioEnum statusUsuario,

    // String nacionalidade, vai para o objeto que altera os dados de perfil do usuario "UsuarioPerfilDTO"

    //Instant dataCadastro,
    
    // @Valid
    // TipoUsuarioEnum tipoUsuario

) {
    
}
