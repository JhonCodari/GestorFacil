package com.JhonCodari.GestorFacil.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.JhonCodari.GestorFacil.dto.AnaliseFinanceiraRespostaDTO;
import com.JhonCodari.GestorFacil.dto.AnaliseCategoriaDTO;
import com.JhonCodari.GestorFacil.model.TransacaoEntity;
import com.JhonCodari.GestorFacil.model.UsuarioEntity;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.repository.TransacaoRepository;
import com.JhonCodari.GestorFacil.service.AnaliseFinanceiraService;
import com.JhonCodari.GestorFacil.service.RelatorioService;
import com.JhonCodari.GestorFacil.service.UsuarioService;

@Service
public class RelatorioServiceImpl implements RelatorioService {

    private final UsuarioService usuarioService;
    private final TransacaoRepository transacaoRepository;
    private final AnaliseFinanceiraService analiseFinanceiraService;

    public RelatorioServiceImpl(
            UsuarioService usuarioService,
            TransacaoRepository transacaoRepository,
            AnaliseFinanceiraService analiseFinanceiraService) {
        this.usuarioService = usuarioService;
        this.transacaoRepository = transacaoRepository;
        this.analiseFinanceiraService = analiseFinanceiraService;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] gerarRelatorioExcel(String emailUsuario, LocalDate dataInicio, LocalDate dataFim) {
        UsuarioEntity usuario = usuarioService.consultarUsuarioPorEmail(new EmailUsuario(emailUsuario));
        AnaliseFinanceiraRespostaDTO analise = analiseFinanceiraService.analisar(emailUsuario, dataInicio, dataFim);
        List<TransacaoEntity> transacoes = transacaoRepository.findAllByUsuarioIdAndDataBetween(
            usuario.getId(), dataInicio, dataFim);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream saida = new ByteArrayOutputStream()) {

            CellStyle estiloTitulo = criarEstiloTitulo(workbook);
            CellStyle estiloCabecalho = criarEstiloCabecalho(workbook);
            CellStyle estiloMoeda = criarEstiloMoeda(workbook);

            criarPlanilhaResumo(workbook, analise, estiloTitulo, estiloCabecalho, estiloMoeda);
            criarPlanilhaTransacoes(workbook, transacoes, estiloTitulo, estiloCabecalho, estiloMoeda);
            criarPlanilhaCategorias(workbook, analise.porCategoria(), estiloTitulo, estiloCabecalho, estiloMoeda);

            workbook.write(saida);
            return saida.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatorio Excel: " + e.getMessage(), e);
        }
    }

    private void criarPlanilhaResumo(Workbook workbook, AnaliseFinanceiraRespostaDTO analise,
                                     CellStyle estiloTitulo, CellStyle estiloCabecalho, CellStyle estiloMoeda) {
        Sheet planilha = workbook.createSheet("Resumo Financeiro");

        int linha = 0;
        Row tituloRow = planilha.createRow(linha++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("Relatorio Financeiro");
        tituloCell.setCellStyle(estiloTitulo);

        linha++;
        criarLinhaResumo(planilha, linha++, "Periodo", analise.periodoInicio() + " a " + analise.periodoFim(), estiloCabecalho, null);
        criarLinhaResumo(planilha, linha++, "Total de Transacoes", String.valueOf(analise.quantidadeTransacoes()), estiloCabecalho, null);
        linha++;
        criarLinhaResumoValor(planilha, linha++, "Total Receitas", analise.totalReceitas().doubleValue(), estiloCabecalho, estiloMoeda);
        criarLinhaResumoValor(planilha, linha++, "Total Despesas", analise.totalDespesas().doubleValue(), estiloCabecalho, estiloMoeda);
        criarLinhaResumoValor(planilha, linha++, "Total Transferencias", analise.totalTransferencias().doubleValue(), estiloCabecalho, estiloMoeda);
        criarLinhaResumoValor(planilha, linha++, "Saldo", analise.saldo().doubleValue(), estiloCabecalho, estiloMoeda);

        if (analise.saldoContaBancaria() != null) {
            criarLinhaResumoValor(planilha, linha, "Saldo Conta Bancaria", analise.saldoContaBancaria().doubleValue(), estiloCabecalho, estiloMoeda);
        }

        planilha.autoSizeColumn(0);
        planilha.autoSizeColumn(1);
    }

    private void criarPlanilhaTransacoes(Workbook workbook, List<TransacaoEntity> transacoes,
                                         CellStyle estiloTitulo, CellStyle estiloCabecalho, CellStyle estiloMoeda) {
        Sheet planilha = workbook.createSheet("Transacoes");

        Row cabecalho = planilha.createRow(0);
        String[] colunas = {"ID", "Descricao", "Valor (R$)", "Tipo", "Categoria", "Data"};
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = cabecalho.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(estiloCabecalho);
        }

        int linhaAtual = 1;
        for (TransacaoEntity t : transacoes) {
            Row row = planilha.createRow(linhaAtual++);
            row.createCell(0).setCellValue(t.getId());
            row.createCell(1).setCellValue(t.getDescricao() != null ? t.getDescricao() : "");

            Cell valorCell = row.createCell(2);
            valorCell.setCellValue(t.getValor().doubleValue());
            valorCell.setCellStyle(estiloMoeda);

            row.createCell(3).setCellValue(t.getTipo().name());
            row.createCell(4).setCellValue(t.getCategoria().name());
            row.createCell(5).setCellValue(t.getData().toString());
        }

        for (int i = 0; i < colunas.length; i++) {
            planilha.autoSizeColumn(i);
        }
    }

    private void criarPlanilhaCategorias(Workbook workbook, List<AnaliseCategoriaDTO> categorias,
                                         CellStyle estiloTitulo, CellStyle estiloCabecalho, CellStyle estiloMoeda) {
        Sheet planilha = workbook.createSheet("Por Categoria");

        Row cabecalho = planilha.createRow(0);
        String[] colunas = {"Categoria", "Tipo", "Total (R$)", "Percentual (%)", "Quantidade"};
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = cabecalho.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(estiloCabecalho);
        }

        int linhaAtual = 1;
        for (AnaliseCategoriaDTO cat : categorias) {
            Row row = planilha.createRow(linhaAtual++);
            row.createCell(0).setCellValue(cat.categoria().name());
            row.createCell(1).setCellValue(cat.tipo().name());

            Cell totalCell = row.createCell(2);
            totalCell.setCellValue(cat.total().doubleValue());
            totalCell.setCellStyle(estiloMoeda);

            row.createCell(3).setCellValue(cat.percentualDoTotal().doubleValue());
            row.createCell(4).setCellValue(cat.quantidade());
        }

        for (int i = 0; i < colunas.length; i++) {
            planilha.autoSizeColumn(i);
        }
    }

    private void criarLinhaResumo(Sheet planilha, int linha, String rotulo, String valor,
                                  CellStyle estiloRotulo, CellStyle estiloValor) {
        Row row = planilha.createRow(linha);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(rotulo);
        labelCell.setCellStyle(estiloRotulo);
        row.createCell(1).setCellValue(valor);
    }

    private void criarLinhaResumoValor(Sheet planilha, int linha, String rotulo, double valor,
                                       CellStyle estiloRotulo, CellStyle estiloMoeda) {
        Row row = planilha.createRow(linha);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(rotulo);
        labelCell.setCellStyle(estiloRotulo);
        Cell valorCell = row.createCell(1);
        valorCell.setCellValue(valor);
        valorCell.setCellStyle(estiloMoeda);
    }

    private CellStyle criarEstiloTitulo(Workbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        Font fonte = workbook.createFont();
        fonte.setBold(true);
        fonte.setFontHeightInPoints((short) 14);
        estilo.setFont(fonte);
        return estilo;
    }

    private CellStyle criarEstiloCabecalho(Workbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        Font fonte = workbook.createFont();
        fonte.setBold(true);
        estilo.setFont(fonte);
        estilo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return estilo;
    }

    private CellStyle criarEstiloMoeda(Workbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        DataFormat formato = workbook.createDataFormat();
        estilo.setDataFormat(formato.getFormat("#,##0.00"));
        return estilo;
    }
}
