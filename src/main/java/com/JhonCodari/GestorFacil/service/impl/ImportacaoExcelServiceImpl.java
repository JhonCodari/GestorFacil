package com.JhonCodari.GestorFacil.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.JhonCodari.GestorFacil.dto.ImportacaoResultadoDTO;
import com.JhonCodari.GestorFacil.exception.ImportacaoExcelException;
import com.JhonCodari.GestorFacil.model.TransacaoEntity;
import com.JhonCodari.GestorFacil.model.UsuarioEntity;
import com.JhonCodari.GestorFacil.model.enums.CategoriaTransacao;
import com.JhonCodari.GestorFacil.model.enums.TipoTransacao;
import com.JhonCodari.GestorFacil.model.valueobjects.EmailUsuario;
import com.JhonCodari.GestorFacil.repository.TransacaoRepository;
import com.JhonCodari.GestorFacil.service.ImportacaoExcelService;
import com.JhonCodari.GestorFacil.service.UsuarioService;

@Service
public class ImportacaoExcelServiceImpl implements ImportacaoExcelService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioService usuarioService;

    public ImportacaoExcelServiceImpl(TransacaoRepository transacaoRepository, UsuarioService usuarioService) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    @Transactional
    public ImportacaoResultadoDTO importarTransacoes(MultipartFile arquivo, String emailUsuario) {
        validarArquivo(arquivo);
        UsuarioEntity usuario = usuarioService.consultarUsuarioPorEmail(new EmailUsuario(emailUsuario));

        List<TransacaoEntity> transacoesValidas = new ArrayList<>();
        List<String> erros = new ArrayList<>();
        int totalLinhas = 0;

        try (Workbook workbook = new XSSFWorkbook(arquivo.getInputStream())) {
            Sheet planilha = workbook.getSheetAt(0);

            for (int i = 1; i <= planilha.getLastRowNum(); i++) {
                Row linha = planilha.getRow(i);
                if (linha == null) continue;
                totalLinhas++;

                try {
                    var transacao = processarLinha(linha, usuario, i);
                    transacoesValidas.add(transacao);
                } catch (Exception e) {
                    erros.add("Linha " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (ImportacaoExcelException e) {
            throw e;
        } catch (Exception e) {
            throw new ImportacaoExcelException("Erro ao processar arquivo Excel: " + e.getMessage());
        }

        if (!transacoesValidas.isEmpty()) {
            transacaoRepository.saveAll(transacoesValidas);
        }

        return new ImportacaoResultadoDTO(totalLinhas, transacoesValidas.size(), erros.size(), erros);
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty())
            throw new ImportacaoExcelException("Arquivo nao pode ser vazio.");

        String nomeArquivo = arquivo.getOriginalFilename();
        if (nomeArquivo == null || !nomeArquivo.endsWith(".xlsx"))
            throw new ImportacaoExcelException("Formato invalido. Envie um arquivo .xlsx");
    }

    private TransacaoEntity processarLinha(Row linha, UsuarioEntity usuario, int indiceLinha) {
        String descricao = lerCelulaTexto(linha, 0);
        BigDecimal valor = lerCelulaNumero(linha, 1, indiceLinha);
        TipoTransacao tipo = lerCelulaTipo(linha, 2, indiceLinha);
        CategoriaTransacao categoria = lerCelulaCategoria(linha, 3, indiceLinha);
        LocalDate data = lerCelulaData(linha, 4, indiceLinha);

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor deve ser positivo.");

        if (tipo == null)
            throw new IllegalArgumentException("Tipo da transacao e obrigatorio.");

        if (categoria == null)
            throw new IllegalArgumentException("Categoria da transacao e obrigatoria.");

        if (data == null)
            throw new IllegalArgumentException("Data da transacao e obrigatoria.");

        return new TransacaoEntity(
            descricao,
            valor.setScale(2, RoundingMode.HALF_UP),
            tipo,
            categoria,
            data,
            usuario
        );
    }

    private String lerCelulaTexto(Row linha, int coluna) {
        Cell celula = linha.getCell(coluna);
        if (celula == null) return null;
        return switch (celula.getCellType()) {
            case STRING -> celula.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) celula.getNumericCellValue());
            default -> null;
        };
    }

    private BigDecimal lerCelulaNumero(Row linha, int coluna, int indiceLinha) {
        Cell celula = linha.getCell(coluna);
        if (celula == null) return null;
        return switch (celula.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(celula.getNumericCellValue());
            case STRING -> {
                try {
                    yield new BigDecimal(celula.getStringCellValue().trim().replace(",", "."));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Valor numerico invalido na coluna 'valor'.");
                }
            }
            default -> null;
        };
    }

    private TipoTransacao lerCelulaTipo(Row linha, int coluna, int indiceLinha) {
        String texto = lerCelulaTexto(linha, coluna);
        if (texto == null || texto.isBlank()) return null;
        try {
            return TipoTransacao.valueOf(texto.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo invalido: '" + texto + "'. Valores aceitos: CREDITO, DEBITO, TRANSFERENCIA.");
        }
    }

    private CategoriaTransacao lerCelulaCategoria(Row linha, int coluna, int indiceLinha) {
        String texto = lerCelulaTexto(linha, coluna);
        if (texto == null || texto.isBlank()) return null;
        try {
            return CategoriaTransacao.valueOf(texto.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoria invalida: '" + texto + "'. Verifique os valores aceitos.");
        }
    }

    private LocalDate lerCelulaData(Row linha, int coluna, int indiceLinha) {
        Cell celula = linha.getCell(coluna);
        if (celula == null) return null;
        return switch (celula.getCellType()) {
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(celula)) {
                    yield celula.getLocalDateTimeCellValue().toLocalDate();
                }
                throw new IllegalArgumentException("Celula numerica na coluna 'data' nao esta formatada como data.");
            }
            case STRING -> {
                try {
                    yield LocalDate.parse(celula.getStringCellValue().trim());
                } catch (Exception e) {
                    throw new IllegalArgumentException("Data invalida. Use o formato: AAAA-MM-DD");
                }
            }
            default -> null;
        };
    }
}
