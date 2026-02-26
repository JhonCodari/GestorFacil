package com.JhonCodari.GestorFacil.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.JhonCodari.GestorFacil.dto.TransacaoAtualizacaoDTO;
import com.JhonCodari.GestorFacil.dto.TransacaoCadastroDTO;
import com.JhonCodari.GestorFacil.dto.TransacaoConvertidaRespostaDTO;
import com.JhonCodari.GestorFacil.dto.TransacaoRespostaDTO;

public interface TransacaoService {

    TransacaoRespostaDTO criar(TransacaoCadastroDTO dto, String emailUsuario);

    Page<TransacaoRespostaDTO> listar(String emailUsuario, Pageable pageable);

    TransacaoRespostaDTO buscarPorId(Long id, String emailUsuario);

    TransacaoRespostaDTO atualizar(Long id, TransacaoAtualizacaoDTO dto, String emailUsuario);

    void deletar(Long id, String emailUsuario);

    Page<TransacaoConvertidaRespostaDTO> listarConvertidas(String emailUsuario, String moeda, Pageable pageable);

    TransacaoConvertidaRespostaDTO buscarPorIdConvertida(Long id, String emailUsuario, String moeda);
}
