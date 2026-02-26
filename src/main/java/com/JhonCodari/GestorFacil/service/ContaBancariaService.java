package com.JhonCodari.GestorFacil.service;

import com.JhonCodari.GestorFacil.dto.ContaBancariaRespostaDTO;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;

public interface ContaBancariaService {

    void vincular(EmailUsuario emailUsuario, String idConta);
    void desvincular(EmailUsuario emailUsuario);
    ContaBancariaRespostaDTO consultarSaldo(EmailUsuario emailUsuario);
}

