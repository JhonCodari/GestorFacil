package com.JhonCodari.GestorFacil.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.JhonCodari.GestorFacil.dto.AnaliseCategoriaDTO;
import com.JhonCodari.GestorFacil.dto.AnaliseFinanceiraRespostaDTO;
import com.JhonCodari.GestorFacil.model.TransacaoEntity;
import com.JhonCodari.GestorFacil.model.UsuarioEntity;
import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.model.valueobjects.NomeCompleto;
import com.JhonCodari.GestorFacil.model.valueobjects.Senha;
import com.JhonCodari.GestorFacil.repository.TransacaoRepository;
import com.JhonCodari.GestorFacil.service.AnaliseFinanceiraService;
import com.JhonCodari.GestorFacil.service.UsuarioService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceImplTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private AnaliseFinanceiraService analiseFinanceiraService;

    @InjectMocks
    private RelatorioServiceImpl relatorioService;

    private UsuarioEntity usuario;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    @BeforeEach
    void configurar() {
        usuario = new UsuarioEntity(
            new NomeCompleto("Joao", "Silva"),
            new EmailUsuario("joao@email.com"),
            new Senha("Senha@123")
        );
        dataInicio = LocalDate.of(2026, 1, 1);
        dataFim = LocalDate.of(2026, 1, 31);
    }

    @Test
    void deveGerarRelatorioExcelComTransacoes() throws Exception {
        var analise = new AnaliseFinanceiraRespostaDTO(
            new BigDecimal("5000.00"),
            new BigDecimal("1500.00"),
            BigDecimal.ZERO,
            new BigDecimal("3500.00"),
            null,
            2,
            dataInicio,
            dataFim,
            List.of(
                new AnaliseCategoriaDTO(CategoriaTransacao.DEPOSITO, TipoTransacao.CREDITO,
                    new BigDecimal("5000.00"), new BigDecimal("76.92"), 1),
                new AnaliseCategoriaDTO(CategoriaTransacao.PAGAMENTO_BOLETO, TipoTransacao.DEBITO,
                    new BigDecimal("1500.00"), new BigDecimal("23.08"), 1)
            )
        );

        var transacao1 = new TransacaoEntity("Salario", new BigDecimal("5000.00"),
            TipoTransacao.CREDITO, CategoriaTransacao.DEPOSITO, LocalDate.of(2026, 1, 15), usuario);
        setId(transacao1, 1L);
        var transacao2 = new TransacaoEntity("Aluguel", new BigDecimal("1500.00"),
            TipoTransacao.DEBITO, CategoriaTransacao.PAGAMENTO_BOLETO, LocalDate.of(2026, 1, 20), usuario);
        setId(transacao2, 2L);

        when(usuarioService.consultarUsuarioPorEmail(any(EmailUsuario.class))).thenReturn(usuario);
        when(analiseFinanceiraService.analisar(eq("joao@email.com"), eq(dataInicio), eq(dataFim))).thenReturn(analise);
        when(transacaoRepository.findAllByUsuarioIdAndDataBetween(any(), eq(dataInicio), eq(dataFim)))
            .thenReturn(List.of(transacao1, transacao2));

        byte[] resultado = relatorioService.gerarRelatorioExcel("joao@email.com", dataInicio, dataFim);

        assertNotNull(resultado);
        assertTrue(resultado.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(resultado))) {
            assertEquals(3, workbook.getNumberOfSheets());
            assertEquals("Resumo Financeiro", workbook.getSheetName(0));
            assertEquals("Transacoes", workbook.getSheetName(1));
            assertEquals("Por Categoria", workbook.getSheetName(2));

            assertEquals(2, workbook.getSheetAt(1).getLastRowNum());
            assertEquals(2, workbook.getSheetAt(2).getLastRowNum());
        }
    }

    @Test
    void deveGerarRelatorioExcelSemTransacoes() throws Exception {
        var analise = new AnaliseFinanceiraRespostaDTO(
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
            null, 0, dataInicio, dataFim, List.of()
        );

        when(usuarioService.consultarUsuarioPorEmail(any(EmailUsuario.class))).thenReturn(usuario);
        when(analiseFinanceiraService.analisar(eq("joao@email.com"), eq(dataInicio), eq(dataFim))).thenReturn(analise);
        when(transacaoRepository.findAllByUsuarioIdAndDataBetween(any(), eq(dataInicio), eq(dataFim)))
            .thenReturn(List.of());

        byte[] resultado = relatorioService.gerarRelatorioExcel("joao@email.com", dataInicio, dataFim);

        assertNotNull(resultado);
        assertTrue(resultado.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(resultado))) {
            assertEquals(3, workbook.getNumberOfSheets());
            assertEquals(0, workbook.getSheetAt(1).getLastRowNum());
            assertEquals(0, workbook.getSheetAt(2).getLastRowNum());
        }
    }

    private void setId(TransacaoEntity entidade, Long id) throws Exception {
        Field campo = TransacaoEntity.class.getDeclaredField("id");
        campo.setAccessible(true);
        campo.set(entidade, id);
    }
}
