package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.LeadNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.CrmGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CustomFieldDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CustomFieldValueDto;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class CrmUseCase {

    private final CrmGateway gateway;

    @Value("${spring.profiles.active}")
    private final String profile;

    public CrmUseCase(
            CrmGateway gateway,
            @Value("${spring.profiles.active}") String profile
    ) {
        this.gateway = gateway;
        this.profile = profile;
    }

    public void atualizarCrm(Vendedor vendedor, Cliente cliente, ConversaAgente conversaAgente) {
        if(profile.equals("prod")) {
            log.info("Atualizando crm. Vendedor: {}, Cliente: {}, Conversa: {}", vendedor, cliente, conversaAgente);

            Integer idLead = this.consultaLeadPeloTelefone(cliente.getTelefone());

            log.info("Construindo body para atualizar card.");

            List<CustomFieldDto> customFieldDtos = new ArrayList<>();

            addTextIfPresent(customFieldDtos, 2760738, cliente.getCpf());

            customFieldDtos.add(selectField(2760990, cliente.getConsentimentoAtendimnento() ? 2191554 : 2191556));

            customFieldDtos.add(selectField(2761160, cliente.getTipoConsulta() == null ? TipoConsulta.NAO_INFORMADO.getCodigoCrm() : cliente.getTipoConsulta().getCodigoCrm()));

            addTextIfPresent(customFieldDtos,2761314, cliente.getDorDesejoPaciente());

            addTextIfPresent(customFieldDtos, 2761366, cliente.getLinkMidia());

            customFieldDtos.add(selectField(2761418, cliente.getPreferenciaHorario() == null ? PreferenciaHorario.NAO_INFORMADO.getCodigoCrm() : cliente.getPreferenciaHorario().getCodigoCrm()));

            Map<String, Integer> tagItem = conversaAgente.getStatus().getCodigo().equals(2) || conversaAgente.getStatus().getCodigo().equals(0)
                    ? Map.of("id", 100738)
                    : Map.of("id", 100736);

            Integer statusId = conversaAgente.getStatus().getCodigo().equals(1) ? 96488527 : 96488979;

            Map<String, Object> embedded = Map.of("tags", List.of(tagItem));

            CardDto cardDto = CardDto.builder()
                    .responsibleUserId(vendedor.getIdVendedorCrm())
                    .statusId(statusId)
                    .customFieldsValues(customFieldDtos)
                    .embedded(embedded)
                    .build();

            log.info("Body para atualizar card criado com sucesso. Body: {}", cardDto);

            try {
                var json = new ObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(cardDto);
                log.info("Kommo PATCH body=\n{}", json);
            } catch (Exception ignore) {
            }

            gateway.atualizarCard(cardDto, idLead);

            log.info("Atualização do crm concluída com sucesso. Card: {}, Id do lead: {}", cardDto, idLead);
        } else {
            log.info("Card atualizado com sucesso !");
        }

    }

    public Integer consultaLeadPeloTelefone(String telefone) {
        log.info("Consultando lead pelo telefone. Telefone: {}", telefone);
        Optional<Integer> lead = gateway.consultaLeadPeloTelefone(telefone);

        if(lead.isEmpty()) {
            throw new LeadNaoEncontradoException();
        }

        log.info("Lead consultado com sucesso. Lead: {}", lead.get());
        return lead.get();
    }

    private CustomFieldDto textField(int fieldId, Object value) {
        return CustomFieldDto.builder()
                .fieldId(fieldId)
                .values(List.of(CustomFieldValueDto.builder()
                        .value(value)
                        .build()))
                .build();
    }

    private CustomFieldDto selectField(int fieldId, Integer... enumIds) {
        var list = java.util.Arrays.stream(enumIds)
                .map(id -> CustomFieldValueDto.builder()
                        .enumId(id)
                        .build())
                .toList();
        return CustomFieldDto.builder()
                .fieldId(fieldId)
                .values(list)
                .build();
    }

    private void addTextIfPresent(List<CustomFieldDto> list, int fieldId, String value) {
        if (value != null && !value.isBlank()) {
            list.add(textField(fieldId, value));
        }
    }
}
