package com.JhonCodari.GestorFacil.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.JhonCodari.GestorFacil.dto.ContaBancariaDTO;
import com.JhonCodari.GestorFacil.dto.ContaBancariaRespostaDTO;
import com.JhonCodari.GestorFacil.exception.ContaBancariaIntegracaoException;
import com.JhonCodari.GestorFacil.exception.ContaBancariaNaoVinculadaException;
import com.JhonCodari.GestorFacil.model.UsuarioEntity;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.repository.UsuarioRepository;
import com.JhonCodari.GestorFacil.service.ContaBancariaService;
import com.JhonCodari.GestorFacil.integration.ContaBancariaClient;

//remover depois de testar
import com.JhonCodari.GestorFacil.integration.CambioClient;
import com.JhonCodari.GestorFacil.dto.CambioRespostaDTO;

@Service
public class ContaBancariaServiceImpl implements ContaBancariaService {

    private final UsuarioRepository usuarioRepository;
    private final ContaBancariaClient contaBancariaClient;

    public ContaBancariaServiceImpl(
            UsuarioRepository usuarioRepository,
            ContaBancariaClient contaBancariaClient) {
        this.usuarioRepository = usuarioRepository;
        this.contaBancariaClient = contaBancariaClient;
    }

    @Override
    @Transactional
    public void vincular(EmailUsuario emailUsuario, String idConta) {
        UsuarioEntity usuario = buscarUsuario(emailUsuario);
        List<ContaBancariaDTO> contas = contaBancariaClient.buscarContasPorUsuarioId(idConta);
        if (contas == null || contas.isEmpty()) {
            throw new ContaBancariaIntegracaoException(
                "Nenhuma conta bancaria encontrada para o usuarioId informado.");
        }
        usuario.vincularContaBancaria(idConta);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void desvincular(EmailUsuario emailUsuario) {
        UsuarioEntity usuario = buscarUsuario(emailUsuario);
        if (!usuario.possuiContaBancariaVinculada()) {
            throw new ContaBancariaNaoVinculadaException(
                "Nenhuma conta bancaria esta vinculada a este usuario.");
        }
        usuario.desvincularContaBancaria();
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public ContaBancariaRespostaDTO consultarSaldo(EmailUsuario emailUsuario) {
        UsuarioEntity usuario = buscarUsuario(emailUsuario);
        if (!usuario.possuiContaBancariaVinculada()) {
            throw new ContaBancariaNaoVinculadaException(
                "Nenhuma conta bancaria esta vinculada a este usuario.");
        }
        List<ContaBancariaDTO> contas = contaBancariaClient.buscarContasPorUsuarioId(usuario.getIdContaBancaria());
        if (contas == null || contas.isEmpty()) {
            throw new ContaBancariaNaoVinculadaException(
                "Nenhuma conta bancaria encontrada para o usuario.");
        }
        return toRespostaDTO(contas.get(0));
    }

    // // metodo provisorio pra testes, 
    // // assim que terminar a integracao com o brasilapi, esse metodo deve ser removido 
    // // e a consulta de cambio deve ser feita diretamente no controller

    // @Override
    // @Transactional(readOnly = true)
    // public CambioRespostaDTO consultarCambio(String moeda) {
    //     String dataOntem = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    //     return cambioClient.consultaCambio(moeda, dataOntem);
    // }

    private UsuarioEntity buscarUsuario(EmailUsuario emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findByEmailValor(emailUsuario.valor());
        if (usuario == null) throw new IllegalArgumentException("Usuario nao encontrado.");
        return usuario;
    }

    private ContaBancariaRespostaDTO toRespostaDTO(ContaBancariaDTO conta) {
        return new ContaBancariaRespostaDTO(
            conta.id(),
            conta.numeroConta(),
            conta.agencia(),
            conta.banco(),
            conta.tipoConta(),
            conta.saldoDisponivel(),
            conta.saldoBloqueado(),
            conta.saldoTotal(),
            conta.limiteChequeEspecial(),
            conta.moeda(),
            conta.ativa(),
            conta.atualizadoEm()
        );
    }
}
