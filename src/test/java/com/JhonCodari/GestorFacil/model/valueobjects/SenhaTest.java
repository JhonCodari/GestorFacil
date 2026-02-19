package com.JhonCodari.GestorFacil.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SenhaTest {

    @Test
    void deveCriarSenhaComValorValido() {
        var senha = new Senha("Senha@123");
        assertNotNull(senha);
        assertEquals("Senha@123", senha.valor());
    }

    @Test
    void deveGerarHashDiferenteDaValorOriginal() {
        var senha = new Senha("Senha@123");
        assertNotEquals("Senha@123", senha.hash());
    }

    @Test
    void deveConfirmaSenhaCorretaContraHash() {
        var senha = new Senha("Senha@123");
        var hash = senha.hash();
        assertTrue(senha.confere(hash));
    }

    @Test
    void deveRejeitarSenhaIncorretaContraHash() {
        var senhaCorreta = new Senha("Senha@123");
        var senhaErrada = new Senha("Errada@456");
        var hash = senhaCorreta.hash();
        assertFalse(senhaErrada.confere(hash));
    }

    @Test
    void deveGerarHashesDiferentesParaMesmaSenha() {
        var senha = new Senha("Senha@123");
        var hash1 = senha.hash();
        var hash2 = senha.hash();
        assertNotEquals(hash1, hash2);
    }
}
