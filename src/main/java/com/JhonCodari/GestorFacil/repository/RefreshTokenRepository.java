package com.JhonCodari.GestorFacil.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.JhonCodari.GestorFacil.model.RefreshTokenEntity;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long>{
    RefreshTokenEntity findByRefreshToken_valor(String valor);
    List<RefreshTokenEntity> findAllByUsuario_Email_Valor(String email);
    
    @Modifying
    @Transactional
    void deleteByDataExpiracaoBefore(Instant dataLimite);
}
