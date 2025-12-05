package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.domain.PreferenciaHorario;
import com.guminteligencia.ura_chatbot_ia.domain.TipoConsulta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GatewayEnumTest {

    @Test
    void deveMapearTipoConsulta() {
        assertEquals(TipoConsulta.ESTETICA_FACIAL_CORPORAL, GatewayEnum.gateayTipoConsultaRelatorio("0"));
        assertEquals(TipoConsulta.SAUDE_CAPILAR, GatewayEnum.gateayTipoConsultaRelatorio("1"));
        assertEquals(TipoConsulta.NAO_INFORMADO, GatewayEnum.gateayTipoConsultaRelatorio("2"));
    }

    @Test
    void deveLancarExcecaoQuandoTipoConsultaInvalido() {
        assertThrows(EscolhaNaoIdentificadoException.class, () -> GatewayEnum.gateayTipoConsultaRelatorio("X"));
    }

    @Test
    void deveMapearPreferenciaHorario() {
        assertEquals(PreferenciaHorario.MANHA, GatewayEnum.gatewayPreferenciaHorarioRelatorio("0"));
        assertEquals(PreferenciaHorario.TARDE, GatewayEnum.gatewayPreferenciaHorarioRelatorio("1"));
        assertEquals(PreferenciaHorario.QUALQUER_HORARIO, GatewayEnum.gatewayPreferenciaHorarioRelatorio("2"));
    }

    @Test
    void deveLancarExcecaoQuandoPreferenciaHorarioInvalida() {
        assertThrows(EscolhaNaoIdentificadoException.class, () -> GatewayEnum.gatewayPreferenciaHorarioRelatorio("X"));
    }
}
