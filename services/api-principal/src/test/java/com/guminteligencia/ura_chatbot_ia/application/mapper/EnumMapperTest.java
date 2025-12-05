package com.guminteligencia.ura_chatbot_ia.application.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.PreferenciaHorario;
import com.guminteligencia.ura_chatbot_ia.domain.TipoConsulta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumMapperTest {

    @Test
    void deveMapearTipoConsulta() {
        assertEquals(TipoConsulta.ESTETICA_FACIAL_CORPORAL, EnumMapper.tipoConsultaMapper(0));
        assertEquals(TipoConsulta.SAUDE_CAPILAR, EnumMapper.tipoConsultaMapper(1));
    }

    @Test
    void deveRetornarTipoConsultaNaoInformadoParaCodigoDesconhecido() {
        assertEquals(TipoConsulta.NAO_INFORMADO, EnumMapper.tipoConsultaMapper(999));
    }

    @Test
    void deveMapearPreferenciaHorario() {
        assertEquals(PreferenciaHorario.MANHA, EnumMapper.preferenciaHorarioMapper(0));
        assertEquals(PreferenciaHorario.TARDE, EnumMapper.preferenciaHorarioMapper(1));
        assertEquals(PreferenciaHorario.QUALQUER_HORARIO, EnumMapper.preferenciaHorarioMapper(2));
    }

    @Test
    void deveRetornarPreferenciaHorarioNaoInformadaParaCodigoDesconhecido() {
        assertEquals(PreferenciaHorario.NAO_INFORMADO, EnumMapper.preferenciaHorarioMapper(-1));
    }
}
