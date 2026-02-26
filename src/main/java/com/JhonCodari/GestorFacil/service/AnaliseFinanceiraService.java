package com.JhonCodari.GestorFacil.service;

import java.time.LocalDate;

import com.JhonCodari.GestorFacil.dto.AnaliseFinanceiraRespostaDTO;

public interface AnaliseFinanceiraService {

    AnaliseFinanceiraRespostaDTO analisar(String emailUsuario, LocalDate dataInicio, LocalDate dataFim);
}
