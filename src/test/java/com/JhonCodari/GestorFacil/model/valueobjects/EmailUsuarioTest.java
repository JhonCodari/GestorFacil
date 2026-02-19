package com.JhonCodari.GestorFacil.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailUsuarioTest {

    @Test
    void deveCriarEmailComValorValido() {
        var email = new EmailUsuario("usuario@email.com");
        assertEquals("usuario@email.com", email.valor());
    }

    @Test
    void deveTratarEmailsComSubdominio() {
        var email = new EmailUsuario("usuario@sub.email.com.br");
        assertNotNull(email);
    }

    @Test
    void devePermitirEmailComMaisDeUmPonto() {
        var email = new EmailUsuario("joao.silva@email.com");
        assertNotNull(email);
    }
}
