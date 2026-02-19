package com.JhonCodari.GestorFacil.model.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TokenConfirmacaoEmailTest {

    private static final Pattern UUID_PATTERN =
        Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    @Test
    void deveGerarTokenComFormatoUUID() {
        var token = TokenConfirmacaoEmail.gerar();
        assertNotNull(token);
        assertTrue(UUID_PATTERN.matcher(token.valor()).matches());
    }

    @Test
    void deveGerarTokensUnicos() {
        var token1 = TokenConfirmacaoEmail.gerar();
        var token2 = TokenConfirmacaoEmail.gerar();
        assertNotEquals(token1.valor(), token2.valor());
    }

    @Test
    void deveCriarTokenComValorExplicito() {
        var uuid = "550e8400-e29b-41d4-a716-446655440000";
        var token = new TokenConfirmacaoEmail(uuid);
        assertEquals(uuid, token.valor());
    }
}
