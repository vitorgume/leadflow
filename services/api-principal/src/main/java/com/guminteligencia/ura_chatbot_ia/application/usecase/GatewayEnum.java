package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.domain.PreferenciaHorario;
import com.guminteligencia.ura_chatbot_ia.domain.TipoConsulta;

public class GatewayEnum {

    public static TipoConsulta gateayTipoConsultaRelatorio(String tipo) {
        String mensagemFormatada = tipo.toLowerCase();

        return switch (mensagemFormatada) {
            case "0" -> TipoConsulta.ESTETICA_FACIAL_CORPORAL;
            case "1" -> TipoConsulta.SAUDE_CAPILAR;
            case "2" -> TipoConsulta.NAO_INFORMADO;
            default -> throw new EscolhaNaoIdentificadoException();
        };
    }

    public static PreferenciaHorario gatewayPreferenciaHorarioRelatorio(String tipo) {
        String mensagemFormatada = tipo.toLowerCase();

        return switch (mensagemFormatada) {
            case "0" -> PreferenciaHorario.MANHA;
            case "1" -> PreferenciaHorario.TARDE;
            case "2" -> PreferenciaHorario.QUALQUER_HORARIO;
            default -> throw new EscolhaNaoIdentificadoException();
        };
    }
}
