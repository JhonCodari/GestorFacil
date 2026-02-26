package com.JhonCodari.GestorFacil.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.JhonCodari.GestorFacil.dto.AnaliseCategoriaDTO;
import com.JhonCodari.GestorFacil.dto.AnaliseFinanceiraConvertidaRespostaDTO;
import com.JhonCodari.GestorFacil.dto.AnaliseFinanceiraRespostaDTO;
import com.JhonCodari.GestorFacil.exception.ContaBancariaNaoVinculadaException;
import com.JhonCodari.GestorFacil.model.TransacaoEntity;
import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.repository.TransacaoRepository;
import com.JhonCodari.GestorFacil.service.AnaliseFinanceiraService;
import com.JhonCodari.GestorFacil.service.CambioConversorService;
import com.JhonCodari.GestorFacil.service.ContaBancariaService;
import com.JhonCodari.GestorFacil.service.UsuarioService;

@Service
public class AnaliseFinanceiraServiceImpl implements AnaliseFinanceiraService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioService usuarioService;
    private final ContaBancariaService contaBancariaService;
    private final CambioConversorService cambioConversorService;

    public AnaliseFinanceiraServiceImpl(
            TransacaoRepository transacaoRepository,
            UsuarioService usuarioService,
            ContaBancariaService contaBancariaService,
            CambioConversorService cambioConversorService) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioService = usuarioService;
        this.contaBancariaService = contaBancariaService;
        this.cambioConversorService = cambioConversorService;
    }

    @Override
    @Transactional(readOnly = true)
    public AnaliseFinanceiraRespostaDTO analisar(String emailUsuario, LocalDate dataInicio, LocalDate dataFim) {
        var usuario = usuarioService.consultarUsuarioPorEmail(new EmailUsuario(emailUsuario));

        List<TransacaoEntity> transacoes = buscarTransacoes(usuario.getId(), dataInicio, dataFim);

        BigDecimal totalReceitas = somarPorTipo(transacoes, TipoTransacao.CREDITO);
        BigDecimal totalDespesas = somarPorTipo(transacoes, TipoTransacao.DEBITO);
        BigDecimal totalTransferencias = somarPorTipo(transacoes, TipoTransacao.TRANSFERENCIA);
        BigDecimal totalMovimentado = totalReceitas.add(totalDespesas).add(totalTransferencias);
        BigDecimal saldo = totalReceitas.subtract(totalDespesas);
        BigDecimal saldoContaBancaria = buscarSaldoContaBancaria(emailUsuario);

        List<AnaliseCategoriaDTO> porCategoria = construirAnalisePorCategoria(transacoes, totalMovimentado);

        return new AnaliseFinanceiraRespostaDTO(
            totalReceitas,
            totalDespesas,
            totalTransferencias,
            saldo,
            saldoContaBancaria,
            transacoes.size(),
            dataInicio,
            dataFim,
            porCategoria
        );
    }

     @Override
    @Transactional(readOnly = true)
    public AnaliseFinanceiraConvertidaRespostaDTO analisarConvertida(
            String emailUsuario, LocalDate dataInicio, LocalDate dataFim, String moeda) {
        AnaliseFinanceiraRespostaDTO analise = analisar(emailUsuario, dataInicio, dataFim);
        BigDecimal taxa = cambioConversorService.buscarTaxaFechamentoPTAX(moeda);

        List<AnaliseCategoriaDTO> categoriasConvertidas = analise.porCategoria().stream()
            .map(c -> new AnaliseCategoriaDTO(
                c.categoria(),
                c.tipo(),
                cambioConversorService.converter(c.total(), taxa),
                c.percentualDoTotal(),
                c.quantidade()
            ))
            .collect(Collectors.toList());

        return new AnaliseFinanceiraConvertidaRespostaDTO(
            moeda.toUpperCase(),
            cambioConversorService.converter(analise.totalReceitas(), taxa),
            cambioConversorService.converter(analise.totalDespesas(), taxa),
            cambioConversorService.converter(analise.totalTransferencias(), taxa),
            cambioConversorService.converter(analise.saldo(), taxa),
            cambioConversorService.converter(analise.saldoContaBancaria(), taxa),
            analise.quantidadeTransacoes(),
            analise.periodoInicio(),
            analise.periodoFim(),
            categoriasConvertidas
        );
    }

    private BigDecimal buscarSaldoContaBancaria(String emailUsuario) {
        try {
            return contaBancariaService.consultarSaldo(new EmailUsuario(emailUsuario)).saldoTotal();
        } catch (ContaBancariaNaoVinculadaException ignorado) {
            return null;
        }
    }

    private List<TransacaoEntity> buscarTransacoes(Long usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        boolean periodoInformado = dataInicio != null && dataFim != null;
        if (periodoInformado) {
            return transacaoRepository.findAllByUsuarioIdAndDataBetween(usuarioId, dataInicio, dataFim);
        }
        return transacaoRepository.findAllByUsuarioId(usuarioId);
    }

    private BigDecimal somarPorTipo(List<TransacaoEntity> transacoes, TipoTransacao tipo) {
        return transacoes.stream()
            .filter(t -> t.getTipo() == tipo)
            .map(TransacaoEntity::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<AnaliseCategoriaDTO> construirAnalisePorCategoria(
            List<TransacaoEntity> transacoes,
            BigDecimal totalMovimentado) {

        Map<CategoriaTransacao, List<TransacaoEntity>> agrupado = transacoes.stream()
            .collect(Collectors.groupingBy(TransacaoEntity::getCategoria));

        return agrupado.entrySet().stream()
            .map(entrada -> {
                CategoriaTransacao categoria = entrada.getKey();
                List<TransacaoEntity> grupo = entrada.getValue();

                BigDecimal totalCategoria = grupo.stream()
                    .map(TransacaoEntity::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                TipoTransacao tipoPrevalente = resolverTipoPrevalente(grupo);

                BigDecimal percentual = totalMovimentado.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : totalCategoria
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalMovimentado, 2, RoundingMode.HALF_UP);

                return new AnaliseCategoriaDTO(categoria, tipoPrevalente, totalCategoria, percentual, grupo.size());
            })
            .sorted(Comparator.comparing(AnaliseCategoriaDTO::total).reversed())
            .collect(Collectors.toList());
    }

    private TipoTransacao resolverTipoPrevalente(List<TransacaoEntity> grupo) {
        return grupo.stream()
            .collect(Collectors.groupingBy(TransacaoEntity::getTipo, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElseThrow();
    }   
}
