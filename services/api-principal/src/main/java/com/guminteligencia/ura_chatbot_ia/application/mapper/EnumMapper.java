package com.guminteligencia.ura_chatbot_ia.application.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.PreferenciaHorario;
import com.guminteligencia.ura_chatbot_ia.domain.TipoConsulta;

public class EnumMapper {

    public static TipoConsulta tipoConsultaMapper(int codigo) {
        return switch (codigo) {
            case 0 -> TipoConsulta.ESTETICA_FACIAL_CORPORAL;
            case 1 -> TipoConsulta.SAUDE_CAPILAR;
            default -> TipoConsulta.NAO_INFORMADO;
        };
    }

    public static PreferenciaHorario preferenciaHorarioMapper(int codigo) {
        return switch (codigo) {
            case 0 -> PreferenciaHorario.MANHA;
            case 1 -> PreferenciaHorario.TARDE;
            case 2 -> PreferenciaHorario.QUALQUER_HORARIO;
            default -> PreferenciaHorario.NAO_INFORMADO;
        };
    }
}
