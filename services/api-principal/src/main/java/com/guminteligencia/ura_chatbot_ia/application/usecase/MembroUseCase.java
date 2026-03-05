package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.MembroComMesmoNumeroJaCadastradoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.MembroNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MembroUseCase {

    private final com.guminteligencia.ura_chatbot_ia.application.gateways.MembroGateway gateway;
    private final UsuarioUseCase usuarioUseCase;

    public Membro cadastrar(Membro novoMembro) {
        Optional<Membro> membroExistente = gateway.consultarPorTelefone(novoMembro.getTelefone());

        if(membroExistente.isPresent()) {
            throw new MembroComMesmoNumeroJaCadastradoException();
        }

        Usuario usuario = usuarioUseCase.consultarPorId(novoMembro.getUsuario().getId());

        novoMembro.setUsuario(usuario);

        return gateway.salvar(novoMembro);
    }

    public List<Membro> listar(UUID idUsuario) {
        return gateway.listar(idUsuario);
    }

    public Membro alterar(Membro novosDados, UUID idMembro) {
        Membro membroExistente = this.consultarPorId(idMembro);

        if(!novosDados.getTelefone().equals(membroExistente.getTelefone())) {
            Optional<Membro> membroExistenteOptional = gateway.consultarPorTelefone(novosDados.getTelefone());
            if(membroExistenteOptional.isPresent()) {
                throw new MembroComMesmoNumeroJaCadastradoException();
            }
        }

        membroExistente.setDados(novosDados);

        return gateway.salvar(membroExistente);
    }

    public Membro consultarPorId(UUID idMembro) {
        Optional<Membro> membro = gateway.consultarPorId(idMembro);

        if(membro.isEmpty()) {
            throw new MembroNaoEncontradoException();
        }

        return membro.get();
    }

    public void deletar(UUID id) {
        this.consultarPorId(id);
        gateway.deletar(id);
    }
}
