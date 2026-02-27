package com.JhonCodari.GestorFacil.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.JhonCodari.GestorFacil.dto.TransacaoAtualizacaoDTO;
import com.JhonCodari.GestorFacil.dto.TransacaoCadastroDTO;
import com.JhonCodari.GestorFacil.dto.TransacaoConvertidaRespostaDTO;
import com.JhonCodari.GestorFacil.dto.TransacaoRespostaDTO;
import com.JhonCodari.GestorFacil.exception.TransacaoNaoEncontradaException;
import com.JhonCodari.GestorFacil.exception.TransacaoNaoPertenceAoUsuarioException;
import com.JhonCodari.GestorFacil.mapper.TransacaoMapper;
import com.JhonCodari.GestorFacil.model.TransacaoEntity;
import com.JhonCodari.GestorFacil.model.UsuarioEntity;
import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.repository.TransacaoRepository;
import com.JhonCodari.GestorFacil.service.CambioConversorService;
import com.JhonCodari.GestorFacil.service.TransacaoService;
import com.JhonCodari.GestorFacil.service.UsuarioService;

@Service
public class TransacaoServiceImpl implements TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioService usuarioService;
    private final CambioConversorService cambioConversorService;

    public TransacaoServiceImpl(
            TransacaoRepository transacaoRepository,
            UsuarioService usuarioService,
            CambioConversorService cambioConversorService) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioService = usuarioService;
        this.cambioConversorService = cambioConversorService;
    }

    @Override
    @Transactional
    public TransacaoRespostaDTO criar(TransacaoCadastroDTO dto, String emailUsuario) {
        UsuarioEntity usuario = usuarioService.consultarUsuarioPorEmail(new EmailUsuario(emailUsuario));

        var transacao = new TransacaoEntity(
            dto.descricao(),
            dto.valor().setScale(2, RoundingMode.HALF_UP),
            dto.tipo(),
            dto.categoria(),
            dto.data(),
            usuario
        );

        return TransacaoMapper.toDTO(transacaoRepository.save(transacao));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransacaoRespostaDTO> listar(String emailUsuario, Pageable pageable) {
        UsuarioEntity usuario = usuarioService.consultarUsuarioPorEmail(new EmailUsuario(emailUsuario));
        return transacaoRepository.findAllByUsuarioId(usuario.getId(), pageable)
            .map(TransacaoMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransacaoRespostaDTO> filtrar(String emailUsuario, TipoTransacao tipo, CategoriaTransacao categoria,
                                              LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        UsuarioEntity usuario = usuarioService.consultarUsuarioPorEmail(new EmailUsuario(emailUsuario));
        return transacaoRepository.filtrar(usuario.getId(), tipo, categoria, dataInicio, dataFim, pageable)
            .map(TransacaoMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public TransacaoRespostaDTO buscarPorId(Long id, String emailUsuario) {
        TransacaoEntity transacao = buscarTransacaoOuFalhar(id);
        verificarPropriedade(transacao, emailUsuario);
        return TransacaoMapper.toDTO(transacao);
    }

    @Override
    @Transactional
    public TransacaoRespostaDTO atualizar(Long id, TransacaoAtualizacaoDTO dto, String emailUsuario) {
        TransacaoEntity transacao = buscarTransacaoOuFalhar(id);
        verificarPropriedade(transacao, emailUsuario);

        if (dto.descricao() != null) transacao.setDescricao(dto.descricao());
        if (dto.valor() != null) transacao.setValor(dto.valor().setScale(2, RoundingMode.HALF_UP));
        if (dto.tipo() != null) transacao.setTipo(dto.tipo());
        if (dto.categoria() != null) transacao.setCategoria(dto.categoria());
        if (dto.data() != null) transacao.setData(dto.data());

        return TransacaoMapper.toDTO(transacaoRepository.save(transacao));
    }

    @Override
    @Transactional
    public void deletar(Long id, String emailUsuario) {
        TransacaoEntity transacao = buscarTransacaoOuFalhar(id);
        verificarPropriedade(transacao, emailUsuario);
        transacaoRepository.delete(transacao);
    }

    private TransacaoEntity buscarTransacaoOuFalhar(Long id) {
        return transacaoRepository.findById(id)
            .orElseThrow(() -> new TransacaoNaoEncontradaException(
                "Transacao com ID " + id + " nao encontrada."));
    }

    private void verificarPropriedade(TransacaoEntity transacao, String emailUsuario) {
        if (!transacao.getUsuario().getEnderecoEmail().equals(emailUsuario)) {
            throw new TransacaoNaoPertenceAoUsuarioException(
                "Transacao nao pertence ao usuario autenticado.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransacaoConvertidaRespostaDTO> listarConvertidas(String emailUsuario, String moeda, Pageable pageable) {
        UsuarioEntity usuario = usuarioService.consultarUsuarioPorEmail(new EmailUsuario(emailUsuario));
        BigDecimal taxa = cambioConversorService.buscarTaxaFechamentoPTAX(moeda);
        return transacaoRepository.findAllByUsuarioId(usuario.getId(), pageable)
            .map(t -> toConvertidaDTO(t, moeda, taxa));
    }

    @Override
    @Transactional(readOnly = true)
    public TransacaoConvertidaRespostaDTO buscarPorIdConvertida(Long id, String emailUsuario, String moeda) {
        TransacaoEntity transacao = buscarTransacaoOuFalhar(id);
        verificarPropriedade(transacao, emailUsuario);
        BigDecimal taxa = cambioConversorService.buscarTaxaFechamentoPTAX(moeda);
        return toConvertidaDTO(transacao, moeda, taxa);
    }

    private TransacaoConvertidaRespostaDTO toConvertidaDTO(TransacaoEntity transacao, String moeda, BigDecimal taxa) {
        return new TransacaoConvertidaRespostaDTO(
            transacao.getId(),
            transacao.getDescricao(),
            transacao.getValor(),
            cambioConversorService.converter(transacao.getValor(), taxa),
            moeda.toUpperCase(),
            transacao.getTipo(),
            transacao.getCategoria(),
            transacao.getData(),
            transacao.getCriadoEm(),
            transacao.getAtualizadoEm()
        );
    }
}
