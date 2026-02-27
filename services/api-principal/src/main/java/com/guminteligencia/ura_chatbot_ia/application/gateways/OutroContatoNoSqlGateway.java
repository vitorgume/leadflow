package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;

import java.util.Optional;
import java.util.UUID;

public interface OutroContatoNoSqlGateway  {

    OutroContato salvar(OutroContato novoContato);
    Optional<OutroContato> consultarPorTelefoneEUsuario(String telefone, UUID idUsuario);

    void deletar(UUID id);
}
