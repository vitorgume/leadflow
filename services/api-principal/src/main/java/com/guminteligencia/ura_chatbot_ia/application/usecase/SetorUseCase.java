package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.SetorComMesmoNomeJaExistenteException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.SetorNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.SetorGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Setor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SetorUseCase {

    private final SetorGateway gateway;
    private final UsuarioUseCase usuarioUseCase;
    private final MembroUseCase membroUseCase;

    public Setor cadastrar(Setor novoSetor) {
        Optional<Setor> setorExistente = gateway.consultarPorNome(novoSetor.getNome(), novoSetor.getUsuario().getId());

        if(setorExistente.isPresent()) {
            throw new SetorComMesmoNomeJaExistenteException();
        }

        Usuario usuario = usuarioUseCase.consultarPorId(novoSetor.getUsuario().getId());

        novoSetor.setUsuario(usuario);

        List<Membro> membros = novoSetor.getMembros().stream()
                .map(membro -> membroUseCase.consultarPorId(membro.getId()))
                .toList();

        novoSetor.setMembros(membros);
        novoSetor.setDataCriacao(LocalDateTime.now());

        return gateway.salvar(novoSetor);
    }

    public List<Setor> listar(UUID idUsuario) {
        return gateway.listar(idUsuario);
    }

    public Setor alterar(Setor novosDados, UUID id) {
        Setor setorExistente = this.consultarPorId(id);

        if(!novosDados.getNome().equals(setorExistente.getNome())) {
            Optional<Setor> setorExistenteOptional = gateway.consultarPorNome(novosDados.getNome());
            if(setorExistenteOptional.isPresent()) {
                throw new SetorComMesmoNomeJaExistenteException();
            }
        }

        if(novosDados.getMembros() == null || novosDados.getMembros().isEmpty()) {
            novosDados.setMembros(setorExistente.getMembros());
        } else {
            List<Membro> membros = novosDados.getMembros().stream()
                    .map(membro -> membroUseCase.consultarPorId(membro.getId()))
                    .toList();

            setorExistente.setMembros(membros);
        }

        setorExistente.setDados(novosDados);

        return gateway.salvar(setorExistente);
    }

    public void deletar(UUID id) {
        this.consultarPorId(id);
        gateway.deletar(id);
    }

    private Setor consultarPorId(UUID id) {
        Optional<Setor> setor = gateway.consultarPorId(id);

        if(setor.isEmpty()) {
            throw new SetorNaoEncontradoException();
        }

        return setor.get();
    }

    public Setor consultarPorNome(String nomeSetor, UUID idUsuario) {
        Optional<Setor> setor = gateway.consultarPorNome(nomeSetor, idUsuario);

        if(setor.isEmpty()) {
            throw new SetorNaoEncontradoException();
        }

        return setor.get();
    }
}
