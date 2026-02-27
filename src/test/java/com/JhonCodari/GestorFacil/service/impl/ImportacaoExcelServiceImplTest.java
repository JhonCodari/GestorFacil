package com.JhonCodari.GestorFacil.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.JhonCodari.GestorFacil.exception.ImportacaoExcelException;
import com.JhonCodari.GestorFacil.model.UsuarioEntity;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.model.valueobjects.NomeCompleto;
import com.JhonCodari.GestorFacil.model.valueobjects.Senha;
import com.JhonCodari.GestorFacil.repository.TransacaoRepository;
import com.JhonCodari.GestorFacil.service.UsuarioService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportacaoExcelServiceImplTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ImportacaoExcelServiceImpl importacaoExcelService;

    private UsuarioEntity usuario;

    @BeforeEach
    void configurar() {
        usuario = new UsuarioEntity(
            new NomeCompleto("Joao", "Silva"),
            new EmailUsuario("joao@email.com"),
            new Senha("Senha@123")
        );
    }

    @Test
    void deveImportarTransacoesComSucesso() throws Exception {
        when(usuarioService.consultarUsuarioPorEmail(any(EmailUsuario.class))).thenReturn(usuario);
        when(transacaoRepository.saveAll(anyList())).thenReturn(List.of());

        var arquivo = criarArquivoExcelValido();

        var resultado = importacaoExcelService.importarTransacoes(arquivo, "joao@email.com");

        assertEquals(2, resultado.totalLinhas());
        assertEquals(2, resultado.sucesso());
        assertEquals(0, resultado.erros());
        assertTrue(resultado.detalhesErros().isEmpty());
        verify(transacaoRepository, times(1)).saveAll(anyList());
    }

    @Test
    void deveLancarExcecaoQuandoArquivoNulo() {
        assertThrows(ImportacaoExcelException.class, () ->
            importacaoExcelService.importarTransacoes(null, "joao@email.com")
        );
    }

    @Test
    void deveLancarExcecaoQuandoArquivoVazio() {
        var arquivo = new MockMultipartFile("arquivo", "vazio.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);

        assertThrows(ImportacaoExcelException.class, () ->
            importacaoExcelService.importarTransacoes(arquivo, "joao@email.com")
        );
    }

    @Test
    void deveLancarExcecaoQuandoFormatoInvalido() {
        var arquivo = new MockMultipartFile("arquivo", "dados.csv", "text/csv", "conteudo".getBytes());

        assertThrows(ImportacaoExcelException.class, () ->
            importacaoExcelService.importarTransacoes(arquivo, "joao@email.com")
        );
    }

    @Test
    void deveReportarErrosEmLinhasInvalidas() throws Exception {
        when(usuarioService.consultarUsuarioPorEmail(any(EmailUsuario.class))).thenReturn(usuario);

        var arquivo = criarArquivoExcelComLinhaInvalida();

        var resultado = importacaoExcelService.importarTransacoes(arquivo, "joao@email.com");

        assertEquals(2, resultado.totalLinhas());
        assertEquals(1, resultado.sucesso());
        assertEquals(1, resultado.erros());
        assertFalse(resultado.detalhesErros().isEmpty());
    }

    @Test
    void deveImportarArquivoSemLinhasDeDados() throws Exception {
        when(usuarioService.consultarUsuarioPorEmail(any(EmailUsuario.class))).thenReturn(usuario);

        var arquivo = criarArquivoExcelApenasCabecalho();

        var resultado = importacaoExcelService.importarTransacoes(arquivo, "joao@email.com");

        assertEquals(0, resultado.totalLinhas());
        assertEquals(0, resultado.sucesso());
        verify(transacaoRepository, never()).saveAll(anyList());
    }

    private MockMultipartFile criarArquivoExcelValido() throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            Sheet planilha = workbook.createSheet("Transacoes");

            Row cabecalho = planilha.createRow(0);
            cabecalho.createCell(0).setCellValue("descricao");
            cabecalho.createCell(1).setCellValue("valor");
            cabecalho.createCell(2).setCellValue("tipo");
            cabecalho.createCell(3).setCellValue("categoria");
            cabecalho.createCell(4).setCellValue("data");

            Row linha1 = planilha.createRow(1);
            linha1.createCell(0).setCellValue("Salario");
            linha1.createCell(1).setCellValue(5000.00);
            linha1.createCell(2).setCellValue("CREDITO");
            linha1.createCell(3).setCellValue("DEPOSITO");
            linha1.createCell(4).setCellValue("2026-01-15");

            Row linha2 = planilha.createRow(2);
            linha2.createCell(0).setCellValue("Aluguel");
            linha2.createCell(1).setCellValue(1500.00);
            linha2.createCell(2).setCellValue("DEBITO");
            linha2.createCell(3).setCellValue("PAGAMENTO_BOLETO");
            linha2.createCell(4).setCellValue("2026-01-20");

            workbook.write(saida);
            return new MockMultipartFile("arquivo", "transacoes.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                saida.toByteArray());
        }
    }

    private MockMultipartFile criarArquivoExcelComLinhaInvalida() throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            Sheet planilha = workbook.createSheet("Transacoes");

            Row cabecalho = planilha.createRow(0);
            cabecalho.createCell(0).setCellValue("descricao");
            cabecalho.createCell(1).setCellValue("valor");
            cabecalho.createCell(2).setCellValue("tipo");
            cabecalho.createCell(3).setCellValue("categoria");
            cabecalho.createCell(4).setCellValue("data");

            Row linha1 = planilha.createRow(1);
            linha1.createCell(0).setCellValue("Salario");
            linha1.createCell(1).setCellValue(5000.00);
            linha1.createCell(2).setCellValue("CREDITO");
            linha1.createCell(3).setCellValue("DEPOSITO");
            linha1.createCell(4).setCellValue("2026-01-15");

            Row linha2 = planilha.createRow(2);
            linha2.createCell(0).setCellValue("Invalida");
            linha2.createCell(1).setCellValue(-100);
            linha2.createCell(2).setCellValue("TIPO_INVALIDO");
            linha2.createCell(3).setCellValue("PAGAMENTO_BOLETO");
            linha2.createCell(4).setCellValue("data-errada");

            workbook.write(saida);
            return new MockMultipartFile("arquivo", "transacoes.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                saida.toByteArray());
        }
    }

    private MockMultipartFile criarArquivoExcelApenasCabecalho() throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            Sheet planilha = workbook.createSheet("Transacoes");

            Row cabecalho = planilha.createRow(0);
            cabecalho.createCell(0).setCellValue("descricao");
            cabecalho.createCell(1).setCellValue("valor");
            cabecalho.createCell(2).setCellValue("tipo");
            cabecalho.createCell(3).setCellValue("categoria");
            cabecalho.createCell(4).setCellValue("data");

            workbook.write(saida);
            return new MockMultipartFile("arquivo", "transacoes.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                saida.toByteArray());
        }
    }
}
