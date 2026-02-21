package com.JhonCodari.GestorFacil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.JhonCodari.GestorFacil.model.UsuarioEntity;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long>{
    boolean existsByEmailValor(String valor);
    UsuarioEntity findByEmailValor(String valor);
    
}
