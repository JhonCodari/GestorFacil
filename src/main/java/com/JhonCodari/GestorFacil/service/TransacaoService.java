package com.JhonCodari.GestorFacil.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.JhonCodari.GestorFacil.dto.TransacaoAtualizacaoDTO;
import com.JhonCodari.GestorFacil.dto.TransacaoCadastroDTO;
import com.JhonCodari.GestorFacil.dto.TransacaoConvertidaRespostaDTO;
import com.JhonCodari.GestorFacil.dto.TransacaoRespostaDTO;
import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;

public interface TransacaoService {

    TransacaoRespostaDTO criar(TransacaoCadastroDTO dto, String emailUsuario);

    Page<TransacaoRespostaDTO> listar(String emailUsuario, Pageable pageable);

    Page<TransacaoRespostaDTO> filtrar(String emailUsuario, TipoTransacao tipo, CategoriaTransacao categoria,
                                       LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    TransacaoRespostaDTO buscarPorId(Long id, String emailUsuario);

    TransacaoRespostaDTO atualizar(Long id, TransacaoAtualizacaoDTO dto, String emailUsuario);

    void deletar(Long id, String emailUsuario);

    Page<TransacaoConvertidaRespostaDTO> listarConvertidas(String emailUsuario, String moeda, Pageable pageable);

    TransacaoConvertidaRespostaDTO buscarPorIdConvertida(Long id, String emailUsuario, String moeda);
}
