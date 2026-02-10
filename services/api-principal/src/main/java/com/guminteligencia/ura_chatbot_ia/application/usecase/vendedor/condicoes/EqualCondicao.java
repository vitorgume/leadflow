package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EqualCondicao implements CondicaoType {

    @Override
    public boolean executar(Cliente cliente, Condicao condicao) {
        Map<String, Object> campos = cliente.getAtributosQualificacao();

        Object valor = campos.get(condicao.getCampo());

        return valor.toString().equalsIgnoreCase(condicao.getValor());
    }

    @Override
    public boolean deveExecutar(OperadorLogico operadorLogico) {
        return operadorLogico.equals(OperadorLogico.EQUAL);
    }
}
