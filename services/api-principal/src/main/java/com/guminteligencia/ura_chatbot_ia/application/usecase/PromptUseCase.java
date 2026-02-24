package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.LimiteDeUmPromptJaAtingidoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.PromptNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.PromptGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Prompt;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromptUseCase {

    private final PromptGateway gateway;
    private final UsuarioUseCase usuarioUseCase;

    public Prompt cadastrar(Prompt novoPrompt) {
        Usuario usuario = usuarioUseCase.consultarPorId(novoPrompt.getUsuario().getId());

        novoPrompt.setUsuario(usuario);

        List<Prompt> prompts = this.listar(usuario.getId());

        if(prompts.size() > 1) {
            throw new LimiteDeUmPromptJaAtingidoException();
        }

        return gateway.salvar(novoPrompt);
    }

    public List<Prompt> listar(UUID idUsuario) {
        return gateway.listar(idUsuario);
    }

    public Prompt alterar(UUID idPrompt, Prompt prompt) {
        Prompt promptExistente = this.consultarPorId(idPrompt);

        promptExistente.setDados(prompt);

        return gateway.salvar(prompt);
    }


    public void deletar(UUID idPrompt) {
        this.consultarPorId(idPrompt);
        gateway.deletar(idPrompt);
    }

    private Prompt consultarPorId(UUID idPrompt) {
        Optional<Prompt> prompt = gateway.consultarPorId(idPrompt);

        if(prompt.isEmpty()) {
            throw new PromptNaoEncontradoException();
        }

        return prompt.get();
    }
}
