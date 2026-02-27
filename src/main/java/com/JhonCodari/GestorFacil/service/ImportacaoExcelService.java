package com.JhonCodari.GestorFacil.service;

import org.springframework.web.multipart.MultipartFile;

import com.JhonCodari.GestorFacil.dto.ImportacaoResultadoDTO;

public interface ImportacaoExcelService {

    ImportacaoResultadoDTO importarTransacoes(MultipartFile arquivo, String emailUsuario);
}
