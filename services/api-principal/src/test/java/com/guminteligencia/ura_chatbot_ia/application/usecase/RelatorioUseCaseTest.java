package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.PreferenciaHorario;
import com.guminteligencia.ura_chatbot_ia.domain.TipoConsulta;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioUseCaseTest {

    @Mock
    private ClienteUseCase clienteUseCase;

    @Mock
    private OutroContatoUseCase outroContatoUseCase;

    @Mock
    private MensagemUseCase mensagemUseCase;

    @InjectMocks
    private RelatorioUseCase useCase;

    @Test
    void enviarRelatorioDiarioVendedores_deveGerarArquivoEEnviar() throws Exception {
        LocalDate fixedDate = LocalDate.of(2025, 7, 28); // segunda

        try (MockedStatic<LocalDate> mockDate =
                     mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
            mockDate.when(LocalDate::now).thenReturn(fixedDate);

            RelatorioContatoDto dto = RelatorioContatoDto.builder()
                    .nome("Ana")
                    .telefone("+55119991111")
                    .cpf("123")
                    .consentimentoAtendimnento(true)
                    .tipoConsulta(TipoConsulta.SAUDE_CAPILAR)
                    .dorDesejoPaciente("Dor")
                    .linkMidia("http://midia")
                    .preferenciaHorario(PreferenciaHorario.MANHA)
                    .dataCriacao(LocalDateTime.of(2025,7,30,9,0))
                    .nomeVendedor("Joao")
                    .build();
            when(clienteUseCase.getRelatorioSegundaFeira()).thenReturn(List.of(dto));

            OutroContato gerente = mock(OutroContato.class);
            when(outroContatoUseCase.consultarPorNome("")).thenReturn(gerente);
            when(gerente.getTelefone()).thenReturn("+55000000001");

            useCase.enviarRelatorioDiarioVendedores();

            ArgumentCaptor<String> arquivoCap = ArgumentCaptor.forClass(String.class);
            verify(mensagemUseCase).enviarRelatorio(arquivoCap.capture(), eq("Relatorio.xlsx"), eq("+55000000001"));

            byte[] decoded = Base64.getDecoder().decode(arquivoCap.getValue());
            try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(decoded))) {
                Sheet sheet = wb.getSheet("Contatos");

                Row header = sheet.getRow(0);
                assertEquals("Nome", header.getCell(0).getStringCellValue());
                assertEquals("Telefone", header.getCell(1).getStringCellValue());
                assertEquals("Cpf", header.getCell(2).getStringCellValue());
                assertEquals("Consentimento Atendimento", header.getCell(3).getStringCellValue());
                assertEquals("Tipo Consulta", header.getCell(4).getStringCellValue());
                assertEquals("Dor / Desejo do paciente", header.getCell(5).getStringCellValue());
                assertEquals("Link da Mídia", header.getCell(6).getStringCellValue());
                assertEquals("Preferência Horário", header.getCell(7).getStringCellValue());
                assertEquals("Data de criação", header.getCell(8).getStringCellValue());
                assertEquals("Nome vendedor", header.getCell(9).getStringCellValue());

                Row row1 = sheet.getRow(1);
                assertEquals("Ana", row1.getCell(0).getStringCellValue());
                assertEquals("+55119991111", row1.getCell(1).getStringCellValue());
                assertEquals("123", row1.getCell(2).getStringCellValue());
                assertTrue(row1.getCell(3).getBooleanCellValue());
                assertEquals(TipoConsulta.SAUDE_CAPILAR.getDescricao(), row1.getCell(4).getStringCellValue());
                assertEquals("Dor", row1.getCell(5).getStringCellValue());
                assertEquals("http://midia", row1.getCell(6).getStringCellValue());
                assertEquals(PreferenciaHorario.MANHA.getDescricao(), row1.getCell(7).getStringCellValue());
                assertEquals(dto.getDataCriacao().toString(), row1.getCell(8).getStringCellValue());
                assertEquals("Joao", row1.getCell(9).getStringCellValue());
            }
        }
    }
}
