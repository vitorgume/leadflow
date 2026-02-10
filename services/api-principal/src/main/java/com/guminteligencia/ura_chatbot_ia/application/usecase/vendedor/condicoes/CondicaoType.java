package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;

public interface CondicaoType {
    boolean executar(Cliente cliente, Condicao condicao);
    boolean deveExecutar(OperadorLogico operadorLogico);
}
