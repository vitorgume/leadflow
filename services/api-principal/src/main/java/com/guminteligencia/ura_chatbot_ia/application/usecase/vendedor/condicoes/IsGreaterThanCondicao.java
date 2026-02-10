package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class IsGreaterThanCondicao implements CondicaoType {

    @Override
    public boolean executar(Cliente cliente, Condicao condicao) {
        Map<String, Object> campos = cliente.getAtributosQualificacao();
        int valor;
        int valorComparacao;

        try {
            valor = Integer.parseInt(campos.get(condicao.getCampo()).toString());
        } catch (NumberFormatException ex) {
            log.error("Erro ao transformar valor do cliente para o tipo inteiro. Operador: {}", condicao.getOperadorLogico());
            return false;
        }

        try {
            valorComparacao = Integer.parseInt(condicao.getValor());
        } catch (NumberFormatException ex) {
            log.error("Erro ao transformar valor de comparação para o tipo inteiro. Operador: {}", condicao.getOperadorLogico());
            return false;
        }

        return valor > valorComparacao;
    }

    @Override
    public boolean deveExecutar(OperadorLogico operadorLogico) {
        return operadorLogico.equals(OperadorLogico.IS_GREATER_THAN);
    }
}
