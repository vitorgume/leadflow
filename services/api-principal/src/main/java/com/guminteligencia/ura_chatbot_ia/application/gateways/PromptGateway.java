package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Prompt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromptGateway {
    Prompt salvar(Prompt novoPrompt);

    List<Prompt> listar(UUID idUsuario);

    Optional<Prompt> consultarPorId(UUID idPrompt);

    void deletar(UUID idPrompt);
}
