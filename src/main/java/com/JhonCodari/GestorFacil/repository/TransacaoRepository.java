package com.JhonCodari.GestorFacil.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.JhonCodari.GestorFacil.model.TransacaoEntity;
import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;

public interface TransacaoRepository extends JpaRepository<TransacaoEntity, Long> {

    List<TransacaoEntity> findAllByUsuarioId(Long usuarioId);

    Page<TransacaoEntity> findAllByUsuarioId(Long usuarioId, Pageable pageable);

    List<TransacaoEntity> findAllByUsuarioIdAndTipo(Long usuarioId, TipoTransacao tipo);

    List<TransacaoEntity> findAllByUsuarioIdAndCategoria(Long usuarioId, CategoriaTransacao categoria);

    List<TransacaoEntity> findAllByUsuarioIdAndDataBetween(Long usuarioId, LocalDate inicio, LocalDate fim);

    @Query("SELECT t FROM TransacaoEntity t WHERE t.usuario.id = :usuarioId"
        + " AND (:tipo IS NULL OR t.tipo = :tipo)"
        + " AND (:categoria IS NULL OR t.categoria = :categoria)"
        + " AND (:dataInicio IS NULL OR t.data >= :dataInicio)"
        + " AND (:dataFim IS NULL OR t.data <= :dataFim)")
    Page<TransacaoEntity> filtrar(
        @Param("usuarioId") Long usuarioId,
        @Param("tipo") TipoTransacao tipo,
        @Param("categoria") CategoriaTransacao categoria,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim,
        Pageable pageable);
}
