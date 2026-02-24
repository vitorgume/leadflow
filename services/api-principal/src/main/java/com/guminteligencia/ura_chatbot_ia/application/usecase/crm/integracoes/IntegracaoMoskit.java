package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ConfiguraCrmUsuarioNaoConfiguradaException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.crm.IntegracaoMoskitGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.CriptografiaJCAUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.ContatoMoskitDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.EntityCustomField;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.PayloadMoskit;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.PhoneDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class IntegracaoMoskit implements CrmIntegracaoType {

    private final IntegracaoMoskitGateway gateway;
    private final CriptografiaJCAUseCase criptografiaJCAUseCase;

    @Value("${spring.profiles.active}")
    private String profile;


    @Override
    public void implementacao(Vendedor vendedor, Cliente cliente, ConversaAgente conversaAgente) {

        if (profile.equals("prod") || profile.equals("homo")) {
            log.info("Atualizando crm. Vendedor: {}, Cliente: {}, Conversa: {}", vendedor, cliente, conversaAgente);

            String nomeCliente = cliente.getNome() == null ? "Nome não informado" : cliente.getNome();

            ConfiguracaoCrm configuracaoCrm = cliente.getUsuario().getConfiguracaoCrm();

            if (configuracaoCrm == null || configuracaoCrm.getMapeamentoCampos() == null) {
                throw new ConfiguraCrmUsuarioNaoConfiguradaException();
            }

            String acessToken = criptografiaJCAUseCase.descriptografar(configuracaoCrm.getAcessToken());

            Integer idContato = this.criarContato(cliente, vendedor, acessToken, configuracaoCrm.getCrmUrl());

            List<ContatoMoskitDto> contatoMoskitDtoList = new ArrayList<>(List.of(ContatoMoskitDto.builder().id(idContato).build()));

            Map<String, String> mapeamento = configuracaoCrm.getMapeamentoCampos();

            List<EntityCustomField> entityCustomFields = new ArrayList<>();

            for (Map.Entry<String, Object> dado : cliente.getAtributosQualificacao().entrySet()) {
                String nomeCampoLocal = dado.getKey();
                Object valorCampo = dado.getValue();

                String idCampoCrm = mapeamento.get(nomeCampoLocal);

                if (idCampoCrm != null && valorCampo != null) {
                    String valorString = valorCampo.toString().trim();
                    
                    if (!valorString.isEmpty()) {
                        entityCustomFields.add(new EntityCustomField(idCampoCrm, valorString));
                    }
                }
            }

            Integer statusId = conversaAgente.getStatus().getCodigo().equals(1)
                    ? Integer.valueOf(configuracaoCrm.getIdEtapaInativos()) : Integer.valueOf(configuracaoCrm.getIdEtapaAtivos());

            Map<String, Integer> stage = new HashMap<>(Map.of("id", statusId));

            PayloadMoskit payloadMoskit = PayloadMoskit.builder()
                    .createdBy(Map.of("id", vendedor.getIdVendedorCrm()))
                    .responsible(Map.of("id", vendedor.getIdVendedorCrm()))
                    .name(nomeCliente)
                    .status("OPEN")
                    .contacts(contatoMoskitDtoList)
                    .stage(stage)
                    .entityCustomFields(entityCustomFields)
                    .build();

            gateway.criarNegocio(payloadMoskit, acessToken, configuracaoCrm.getCrmUrl());

            log.info("Atualização do crm concluída com sucesso. Card: {}, Id do lead: {}", payloadMoskit, idContato);
        } else {
            log.info("Negócio atualizado com sucesso.");
        }

    }

    @Override
    public CrmType getCrmType() {
        return CrmType.MOSKIT;
    }


    private Integer criarContato(Cliente cliente, Vendedor vendedor, String acessToken, String crmUrl) {

        String nomeCliente = cliente.getNome() == null ? "Nome não informado" : cliente.getNome();

        ContatoMoskitDto contatoMoskitDto = ContatoMoskitDto.builder()
                .createdBy(Map.of("id", vendedor.getIdVendedorCrm()))
                .responsible(Map.of("id", vendedor.getIdVendedorCrm()))
                .name(nomeCliente)
                .phones(new ArrayList<>(List.of(new PhoneDto(cliente.getTelefone()))))
                .build();

        return gateway.criarContato(contatoMoskitDto, acessToken, crmUrl);
    }
}
