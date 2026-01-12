package com.guminteligencia.ura_chatbot_ia.application.usecase.crm;

import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.CrmIntegracaoFactory;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.CrmIntegracaoType;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrmUseCase {

    private final CrmIntegracaoFactory crmIntegracaoFactory;

    public void atualizarCrm(Vendedor vendedor, Cliente cliente, ConversaAgente conversaAgente) {
        CrmIntegracaoType integracao = crmIntegracaoFactory.create(cliente.getUsuario().getConfiguracaoCrm().getCrmType());
        integracao.implementacao(vendedor, cliente, conversaAgente);
    }

}
