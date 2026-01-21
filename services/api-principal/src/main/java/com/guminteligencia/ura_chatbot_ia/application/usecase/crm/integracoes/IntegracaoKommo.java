package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.ConfiguraCrmUsuarioNaoConfiguradaException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.LeadNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.IntegracaoKommoGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.CriptografiaJCAUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.kommo.CustomFieldDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.kommo.CustomFieldValueDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.kommo.PayloadKommo;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class IntegracaoKommo implements CrmIntegracaoType {

    @Value("${spring.profiles.active}")
    private final String profile;

    private final IntegracaoKommoGateway gateway;
    private final CriptografiaJCAUseCase criptografiaJCAUseCase;

    @Override
    public void implementacao(Vendedor vendedor, Cliente cliente, ConversaAgente conversaAgente) {

        if(!profile.equals("prod")) {
            log.info("Atualizando crm. Vendedor: {}, Cliente: {}, Conversa: {}", vendedor, cliente, conversaAgente);

            ConfiguracaoCrm configuracaoCrm = cliente.getUsuario().getConfiguracaoCrm();

            if(configuracaoCrm == null || configuracaoCrm.getMapeamentoCampos() == null) {
                throw new ConfiguraCrmUsuarioNaoConfiguradaException();
            }
            
            String acessToken = criptografiaJCAUseCase.descriptografar(configuracaoCrm.getAcessToken());

            Integer idLead = this.consultaLeadPeloTelefone(cliente.getTelefone(), acessToken);

            Map<String, String> mapeamento = configuracaoCrm.getMapeamentoCampos();

            log.info("Construindo body para atualizar card.");

            List<CustomFieldDto> customFieldDtos = new ArrayList<>();

            for(Map.Entry<String, Object> dado : cliente.getAtributosQualificacao().entrySet()) {
                String nomeCampoLocal = dado.getKey();
                Object valorCampo = dado.getValue();

                String idCampoCrm = mapeamento.get(nomeCampoLocal);

                if(idCampoCrm != null) {
                    addTextIfPresent(customFieldDtos, idCampoCrm, valorCampo);
                }
            }

            Map<String, Integer> tagItem = conversaAgente.getStatus().getCodigo().equals(2) || conversaAgente.getStatus().getCodigo().equals(0)
                    ? Map.of("id", Integer.valueOf(configuracaoCrm.getIdTagAtivo()))
                    : Map.of("id", Integer.valueOf(configuracaoCrm.getIdTagInativo()));

            Integer statusId = conversaAgente.getStatus().getCodigo().equals(1)
                    ? Integer.valueOf(configuracaoCrm.getIdEtapaInativos()) : Integer.valueOf(configuracaoCrm.getIdEtapaAtivos());

            Map<String, Object> embedded = Map.of("tags", List.of(tagItem));

            PayloadKommo payloadKommo = PayloadKommo.builder()
                    .responsibleUserId(vendedor.getIdVendedorCrm())
                    .statusId(statusId)
                    .customFieldsValues(customFieldDtos)
                    .embedded(embedded)
                    .build();

            log.info("Body para atualizar card criado com sucesso. Body: {}", payloadKommo);

            try {
                var json = new ObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(payloadKommo);
                log.info("Kommo PATCH body=\n{}", json);
            } catch (Exception ignore) {
            }

            gateway.atualizarCard(payloadKommo, idLead, acessToken);

            log.info("Atualização do crm concluída com sucesso. Card: {}, Id do lead: {}", payloadKommo, idLead);
        } else {
            log.info("Card atualizado com sucesso !");
        }
    }

    @Override
    public CrmType getCrmType() {
        return CrmType.KOMMO;
    }

    private void addTextIfPresent(List<CustomFieldDto> list, String fieldId, Object value) {
        if (value != null) {
            list.add(textField(Integer.valueOf(fieldId), value));
        }
    }

    private CustomFieldDto textField(Integer fieldId, Object value) {
        return CustomFieldDto.builder()
                .fieldId(fieldId)
                .values(List.of(CustomFieldValueDto.builder()
                        .value(value)
                        .build()))
                .build();
    }

    private Integer consultaLeadPeloTelefone(String telefone, String acessToken) {
        log.info("Consultando lead pelo telefone. Telefone: {}", telefone);
        Optional<Integer> lead = gateway.consultaLeadPeloTelefone(telefone, acessToken);

        if(lead.isEmpty()) {
            throw new LeadNaoEncontradoException();
        }

        log.info("Lead consultado com sucesso. Lead: {}", lead.get());
        return lead.get();
    }

}
