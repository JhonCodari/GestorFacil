package com.JhonCodari.GestorFacil.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.JhonCodari.GestorFacil.model.RefreshTokenEntity;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long>{
    RefreshTokenEntity findByRefreshToken_valor(String valor);
}
