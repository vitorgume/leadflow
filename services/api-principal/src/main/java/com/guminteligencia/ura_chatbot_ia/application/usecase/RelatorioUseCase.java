package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.OutroContatoNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.TipoContato;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ObjetoRelatorioDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioUseCase {

    private final ClienteUseCase clienteUseCase;
    private final OutroContatoUseCase outroContatoUseCase;
    private final MensagemUseCase mensagemUseCase;
    private final UsuarioUseCase usuarioUseCase;

    @Scheduled(cron = "0 35 17 * * MON-FRI")
    public void enviarRelatorioDiarioVendedores() {
        log.info("Gerando relatório de contatos dos vendedores.");
        DayOfWeek dataHoje = LocalDate.now().getDayOfWeek();

        List<Usuario> usuarios = usuarioUseCase.listar();


        usuarios.forEach(usuario -> {
            List<ObjetoRelatorioDto> relatorio;

            if (dataHoje.equals(DayOfWeek.MONDAY)) {
                relatorio = clienteUseCase.getRelatorioSegundaFeira(usuario.getId());
            } else {
                relatorio = clienteUseCase.getRelatorio(usuario.getId());
            }

            OutroContato gerencia = null;

            try {
                gerencia = outroContatoUseCase.consultarPorTipo(TipoContato.GERENTE, usuario.getId());
            } catch (OutroContatoNaoEncontradoException ex) {
                log.warn("Nenhum contato encontrado para usuario: {}", usuario);
            }

            if(gerencia != null) {
                String arquivo = gerarArquivo(relatorio);

                mensagemUseCase.enviarRelatorio(arquivo, "Relatorio.xlsx", gerencia.getTelefone());
            }
        });

        log.info("Geração de relatório dos contatos dos vendedores concluída com sucesso.");
    }

    private String gerarArquivo(List<ObjetoRelatorioDto> contatos) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Contatos");

            List<String> attributeHeaders = new ArrayList<>();
            if (contatos != null && !contatos.isEmpty()) {
                Set<String> headerSet = new LinkedHashSet<>();
                for (ObjetoRelatorioDto contato : contatos) {
                    if (contato.getAtributos_qualificacao() != null) {
                        headerSet.addAll(contato.getAtributos_qualificacao().keySet());
                    }
                }
                attributeHeaders.addAll(headerSet);
            }

            Row header = sheet.createRow(0);
            int cellIdx = 0;
            header.createCell(cellIdx++).setCellValue("Nome");
            header.createCell(cellIdx++).setCellValue("Telefone");

            for (String attributeHeader : attributeHeaders) {
                header.createCell(cellIdx++).setCellValue(attributeHeader);
            }

            header.createCell(cellIdx++).setCellValue("Data de criação");
            header.createCell(cellIdx++).setCellValue("Nome vendedor");

            int rowNum = 1;
            if (contatos != null) {
                for (ObjetoRelatorioDto dto : contatos) {
                    Row row = sheet.createRow(rowNum++);
                    int dataCellIdx = 0;
                    row.createCell(dataCellIdx++).setCellValue(dto.getNome());
                    row.createCell(dataCellIdx++).setCellValue(dto.getTelefone());

                    for (String attributeHeader : attributeHeaders) {
                        String value = dto.getAtributos_qualificacao() != null
                                ? dto.getAtributos_qualificacao().getOrDefault(attributeHeader, "")
                                : "";
                        row.createCell(dataCellIdx++).setCellValue(value);
                    }

                    row.createCell(dataCellIdx++).setCellValue(dto.getData_criacao() != null ? dto.getData_criacao().toString() : "");
                    row.createCell(dataCellIdx++).setCellValue(dto.getNome_vendedor());
                }
            }

            for (int i = 0; i < cellIdx; i++) {
                sheet.autoSizeColumn(i);
            }

            byte[] planilha;

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                planilha = out.toByteArray();
            }

            return Base64.getEncoder().encodeToString(planilha);
        } catch (IOException ex) {
            log.error("Erro ao gerar relatório de vendedores", ex);
        }

        return "";
    }
}
