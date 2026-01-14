package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
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
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioUseCase {

    private final ClienteUseCase clienteUseCase;
    private final OutroContatoUseCase outroContatoUseCase;
    private final MensagemUseCase mensagemUseCase;

    @Scheduled(cron = "0 0 17 * * MON-FRI")
    public void enviarRelatorioDiarioVendedores() {
        log.info("Gerando relatório de contatos dos vendedores.");
        DayOfWeek dataHoje = LocalDate.now().getDayOfWeek();
        List<RelatorioContatoDto> relatorio;

        if (dataHoje.equals(DayOfWeek.MONDAY)) {
            relatorio = clienteUseCase.getRelatorioSegundaFeira();
        } else {
            relatorio = clienteUseCase.getRelatorio();
        }

        OutroContato gerencia = outroContatoUseCase.consultarPorNome("Lucas");

        String arquivo = gerarArquivo(relatorio);

        mensagemUseCase.enviarRelatorio(arquivo, "Relatorio.xlsx", gerencia.getTelefone());

        log.info("Geração de relatório dos contatos dos vendedores concluída com sucesso.");
    }

    private String gerarArquivo(List<RelatorioContatoDto> contatos) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Contatos");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Nome");
            header.createCell(1).setCellValue("Telefone");
            header.createCell(2).setCellValue("Cpf");
            header.createCell(3).setCellValue("Consentimento Atendimento");
            header.createCell(4).setCellValue("Tipo Consulta");
            header.createCell(5).setCellValue("Dor / Desejo do paciente");
            header.createCell(6).setCellValue("Link da Mídia");
            header.createCell(7).setCellValue("Preferência Horário");
            header.createCell(8).setCellValue("Data de criação");
            header.createCell(9).setCellValue("Nome vendedor");

            int rowNum = 1;
            for (RelatorioContatoDto dto : contatos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(dto.getNome());
                row.createCell(1).setCellValue(dto.getTelefone());
                row.createCell(2).setCellValue(dto.getCpf());
                row.createCell(3).setCellValue(dto.getConsentimentoAtendimnento());
                row.createCell(4).setCellValue(dto.getTipoConsulta().getDescricao());
                row.createCell(5).setCellValue(dto.getDorDesejoPaciente());
                row.createCell(6).setCellValue(dto.getLinkMidia());
                row.createCell(7).setCellValue(dto.getPreferenciaHorario().getDescricao());
                row.createCell(8).setCellValue(dto.getDataCriacao().toString());
                row.createCell(9).setCellValue(dto.getNomeVendedor());
            }

            for (int i = 0; i < 6; i++) {
                sheet.setColumnWidth(i, 6000);
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
