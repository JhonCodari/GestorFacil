package com.JhonCodari.GestorFacil.service;

import java.time.LocalDate;

public interface RelatorioService {

    byte[] gerarRelatorioExcel(String emailUsuario, LocalDate dataInicio, LocalDate dataFim);
}
