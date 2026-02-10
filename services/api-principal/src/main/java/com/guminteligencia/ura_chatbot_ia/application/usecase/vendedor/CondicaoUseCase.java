package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.CondicaoNaoEncontradaException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.CondicaoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CondicaoUseCase {

    private final CondicaoGateway gateway;

    public Condicao cadastrar(Condicao condicao) {
        return gateway.salvar(condicao);
    }

    public Condicao consultarPorId(UUID id) {
        Optional<Condicao> condicao = gateway.consultarPorId(id);

        if(condicao.isEmpty()) {
            throw new CondicaoNaoEncontradaException();
        }

        return condicao.get();
    }

    public Condicao alterar(UUID id, Condicao condicao) {
        Condicao condicaoExistente = this.consultarPorId(id);

        condicaoExistente.setDados(condicao);

        return gateway.salvar(condicaoExistente);
    }

    public void deletar(UUID id) {
        this.consultarPorId(id);
        gateway.deletar(id);
    }
}
