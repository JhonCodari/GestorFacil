package com.JhonCodari.GestorFacil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.JhonCodari.GestorFacil.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    boolean existsByEmailValor(String valor);
    Usuario findByEmailValor(String valor);
    
}
