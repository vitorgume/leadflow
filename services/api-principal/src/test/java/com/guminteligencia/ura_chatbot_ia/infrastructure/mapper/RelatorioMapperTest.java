package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.domain.PreferenciaHorario;
import com.guminteligencia.ura_chatbot_ia.domain.TipoConsulta;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RelatorioMapperTest {

    private Object[] makeRow(String nome,
                             String telefone,
                             String cpf,
                             Boolean consentimento,
                             String tipoConsultaCodigo,
                             String dorDesejoPaciente,
                             String linkMidia,
                             String preferenciaHorarioCodigo,
                             LocalDateTime dataCriacao,
                             String vendedorNome) {
        return new Object[] {
                nome,
                telefone,
                cpf,
                consentimento,
                tipoConsultaCodigo,
                dorDesejoPaciente,
                linkMidia,
                preferenciaHorarioCodigo,
                Timestamp.valueOf(dataCriacao),
                vendedorNome
        };
    }

    @Test
    void deveTransformarParaDtoComDadosValidos() {
        var dataCriacao = LocalDateTime.of(2025, 7, 30, 14, 20);
        Object[] row = makeRow(
                "Ana",
                "+5511999",
                "12345678900",
                true,
                "0",
                "Dor nas costas",
                "https://link.com",
                "1",
                dataCriacao,
                "Joao"
        );

        List<RelatorioContatoDto> dtoList = RelatorioMapper.paraDto(List.<Object[]>of(row));

        assertEquals(1, dtoList.size());
        RelatorioContatoDto dto = dtoList.get(0);

        assertAll("campos principais",
                () -> assertEquals("Ana", dto.getNome()),
                () -> assertEquals("+5511999", dto.getTelefone()),
                () -> assertEquals("12345678900", dto.getCpf()),
                () -> assertEquals(true, dto.getConsentimentoAtendimnento()),
                () -> assertEquals("Dor nas costas", dto.getDorDesejoPaciente()),
                () -> assertEquals("https://link.com", dto.getLinkMidia()),
                () -> assertEquals(dataCriacao, dto.getDataCriacao()),
                () -> assertEquals("Joao", dto.getNomeVendedor())
        );
        assertEquals(TipoConsulta.ESTETICA_FACIAL_CORPORAL, dto.getTipoConsulta());
        assertEquals(PreferenciaHorario.TARDE, dto.getPreferenciaHorario());
    }

    @Test
    void deveAtribuirTipoConsultaNaoInformadoQuandoCodigoInvalido() {
        var dataCriacao = LocalDateTime.of(2025, 7, 30, 14, 20);
        Object[] row = makeRow(
                "Bruno",
                "+5522333",
                "123",
                false,
                "codigo_invalido",
                "Dor de cabeca",
                null,
                "0",
                dataCriacao,
                "Maria"
        );

        RelatorioContatoDto dto = RelatorioMapper.paraDto(List.<Object[]>of(row)).get(0);

        assertEquals(TipoConsulta.NAO_INFORMADO, dto.getTipoConsulta());
        assertEquals(PreferenciaHorario.MANHA, dto.getPreferenciaHorario());
    }

    @Test
    void deveAtribuirPreferenciaHorarioNaoInformadaQuandoCodigoInvalido() {
        var dataCriacao = LocalDateTime.of(2025, 7, 30, 14, 20);
        Object[] row = makeRow(
                "Carla",
                "+5533444",
                "987",
                true,
                "1",
                "Dor abdominal",
                "https://link.com/video",
                "preferencia_invalida",
                dataCriacao,
                "Pedro"
        );

        RelatorioContatoDto dto = RelatorioMapper.paraDto(List.<Object[]>of(row)).get(0);

        assertEquals(TipoConsulta.SAUDE_CAPILAR, dto.getTipoConsulta());
        assertEquals(PreferenciaHorario.NAO_INFORMADO, dto.getPreferenciaHorario());
    }
}
