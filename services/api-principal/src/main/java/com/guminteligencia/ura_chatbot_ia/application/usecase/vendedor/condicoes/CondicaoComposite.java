package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.CondicaoLogicaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CondicaoComposite {

    private final List<CondicaoType> condicoes;

    public CondicaoType escolher(OperadorLogico operadorLogico) {
        return condicoes.stream()
                .filter(condicaoComposite -> condicaoComposite.deveExecutar(operadorLogico))
                .findFirst()
                .orElseThrow(CondicaoLogicaNaoIdentificadoException::new);
    }

}
