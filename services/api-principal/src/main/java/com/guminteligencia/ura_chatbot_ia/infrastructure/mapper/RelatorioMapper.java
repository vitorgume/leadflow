package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.application.usecase.GatewayEnum;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.domain.PreferenciaHorario;
import com.guminteligencia.ura_chatbot_ia.domain.TipoConsulta;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class RelatorioMapper {
    public static List<RelatorioContatoDto> paraDto(List<Object[]> objects) {
        return objects.stream()
                .map(obj -> {
                            RelatorioContatoDto relatorio = RelatorioContatoDto.builder()
                                    .nome((String) obj[0])
                                    .telefone((String) obj[1])
                                    .cpf((String) obj[2])
                                    .consentimentoAtendimnento((Boolean) obj[3])
                                    .dorDesejoPaciente((String) obj[5])
                                    .linkMidia((String) obj[6])
                                    .dataCriacao(((Timestamp) obj[8]).toLocalDateTime())
                                    .nomeVendedor((String) obj[9])
                                    .build();

                            try {
                                relatorio.setTipoConsulta(GatewayEnum.gateayTipoConsultaRelatorio(String.valueOf(obj[4])));
                            } catch (EscolhaNaoIdentificadoException ex) {
                                relatorio.setTipoConsulta(TipoConsulta.NAO_INFORMADO);
                            }

                            try {
                                relatorio.setPreferenciaHorario(GatewayEnum.gatewayPreferenciaHorarioRelatorio(String.valueOf(obj[7])));
                            } catch (EscolhaNaoIdentificadoException ex) {
                                relatorio.setPreferenciaHorario(PreferenciaHorario.NAO_INFORMADO);
                            }


                            return relatorio;
                        }
                )
                .collect(Collectors.toList());
    }
}
