package com.JhonCodari.GestorFacil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.JhonCodari.GestorFacil.model.TokenConfirmacaoEmailEntity;

@Repository
public interface TokenConfirmacaoEmailRepository extends JpaRepository<TokenConfirmacaoEmailEntity, Long> {
    TokenConfirmacaoEmailEntity findByToken_Valor(String valor);
}
