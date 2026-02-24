package com.JhonCodari.GestorFacil.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.JhonCodari.GestorFacil.model.TransacaoEntity;
import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;

public interface TransacaoRepository extends JpaRepository<TransacaoEntity, Long> {

    List<TransacaoEntity> findAllByUsuarioId(Long usuarioId);

    Page<TransacaoEntity> findAllByUsuarioId(Long usuarioId, Pageable pageable);

    List<TransacaoEntity> findAllByUsuarioIdAndTipo(Long usuarioId, TipoTransacao tipo);

    List<TransacaoEntity> findAllByUsuarioIdAndCategoria(Long usuarioId, CategoriaTransacao categoria);

    List<TransacaoEntity> findAllByUsuarioIdAndDataBetween(Long usuarioId, LocalDate inicio, LocalDate fim);
}
