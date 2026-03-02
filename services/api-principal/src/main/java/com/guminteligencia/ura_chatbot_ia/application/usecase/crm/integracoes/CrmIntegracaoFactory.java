package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.IntegracaoExistenteNaoIdentificadaException;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrmIntegracaoFactory {

    private final List<CrmIntegracaoType> integracoes;

    public CrmIntegracaoType create(CrmType crmType) {
        return integracoes.stream()
                .filter(integracao -> integracao.getCrmType().equals(crmType))
                .findFirst()
                .orElseThrow(IntegracaoExistenteNaoIdentificadaException::new);
    }

}
