package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembroGateway {
    Optional<Membro> consultarPorTelefone(String telefone);

    Membro salvar(Membro novoMembro);

    List<Membro> listar(UUID idUsuario);

    Optional<Membro> consultarPorId(UUID idMembro);

    void deletar(UUID id);
}
