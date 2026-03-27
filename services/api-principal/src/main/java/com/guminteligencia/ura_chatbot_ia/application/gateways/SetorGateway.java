package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Setor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SetorGateway {
    Optional<Setor> consultarPorNome(String nome, UUID idUsuario);

    Setor salvar(Setor novoSetor);

    List<Setor> listar(UUID idUsuario);

    Optional<Setor> consultarPorId(UUID id);

    void deletar(UUID id);
}
