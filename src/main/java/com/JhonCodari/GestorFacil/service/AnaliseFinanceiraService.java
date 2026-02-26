package com.JhonCodari.GestorFacil.service;

import java.time.LocalDate;

import com.JhonCodari.GestorFacil.dto.AnaliseFinanceiraConvertidaRespostaDTO;
import com.JhonCodari.GestorFacil.dto.AnaliseFinanceiraRespostaDTO;

public interface AnaliseFinanceiraService {

    AnaliseFinanceiraRespostaDTO analisar(String emailUsuario, LocalDate dataInicio, LocalDate dataFim);

    AnaliseFinanceiraConvertidaRespostaDTO analisarConvertida(String emailUsuario, LocalDate dataInicio, LocalDate dataFim, String moeda);
}
