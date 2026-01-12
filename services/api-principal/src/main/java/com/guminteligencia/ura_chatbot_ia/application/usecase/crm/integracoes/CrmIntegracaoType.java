package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes;

import com.guminteligencia.ura_chatbot_ia.domain.*;

public interface CrmIntegracaoType {
    void implementacao(Vendedor vendedor, Cliente cliente, ConversaAgente conversaAgente);
    CrmType getCrmType();
}
