package com.JhonCodari.GestorFacil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.JhonCodari.GestorFacil.model.TokenRecuperacaoSenhaEntity;

@Repository
public interface TokenRecuperacaoSenhaRepository extends JpaRepository<TokenRecuperacaoSenhaEntity, Long> {
    TokenRecuperacaoSenhaEntity findByToken_Valor(String valor);
}
