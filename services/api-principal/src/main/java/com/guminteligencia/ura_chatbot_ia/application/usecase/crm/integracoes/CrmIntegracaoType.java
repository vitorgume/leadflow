package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes;

import com.guminteligencia.ura_chatbot_ia.domain.*;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;

public interface CrmIntegracaoType {
    void implementacao(Vendedor vendedor, Cliente cliente, ConversaAgente conversaAgente);
    CrmType getCrmType();
}
