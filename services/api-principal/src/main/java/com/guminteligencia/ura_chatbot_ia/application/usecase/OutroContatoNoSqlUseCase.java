package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.OutroContatoNoSqlNaoEcontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.OutroContatoNoSqlGateway;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutroContatoNoSqlUseCase {

    private final OutroContatoNoSqlGateway gateway;

    public OutroContato salvar(OutroContato novoOutroContato) {
        return gateway.salvar(novoOutroContato);
    }

    public OutroContato consultarPorTelefoneEUsuario(String telefone, UUID idUsuario) {
        Optional<OutroContato> outroContato = gateway.consultarPorTelefoneEUsuario(telefone, idUsuario);

        if(outroContato.isEmpty()) {
            throw new OutroContatoNoSqlNaoEcontradoException();
        }

        return outroContato.get();
    }

    public void deletar(String telefone, UUID idUsuario) {
        OutroContato outroContato = this.consultarPorTelefoneEUsuario(telefone, idUsuario);
        gateway.deletar(outroContato.getId());
    }

}
