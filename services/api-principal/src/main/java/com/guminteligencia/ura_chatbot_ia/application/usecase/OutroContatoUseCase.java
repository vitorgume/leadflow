package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.OutroContatoComMesmoTelefoneJaCadastradoExcetion;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.OutroContatoNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.OutroContatoTipoGerenciaJaCadastradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.OutroContatoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.TipoContato;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutroContatoUseCase {

    private final OutroContatoGateway gateway;
    private final UsuarioUseCase usuarioUseCase;

    public OutroContato consultarPorNome(String nome) {
        Optional<OutroContato> outroContato = gateway.consultarPorNome(nome);

        if(outroContato.isEmpty()) {
            throw new OutroContatoNaoEncontradoException();
        }

        return outroContato.get();
    }

    public OutroContato consultarPorTipo(TipoContato tipo, UUID idUsuario) {
        Optional<OutroContato> outroContato = gateway.consultarPorTipo(tipo, idUsuario);

        if(outroContato.isEmpty()) {
            throw new OutroContatoNaoEncontradoException();
        }

        return outroContato.get();
    }

    public OutroContato cadastrar(OutroContato novoOutroContato) {
        Optional<OutroContato> outroContato = gateway.consultarPorTipo(novoOutroContato.getTipoContato(), novoOutroContato.getUsuario().getId());

        if(outroContato.isPresent()) {
            if(outroContato.get().getTipoContato().equals(TipoContato.GERENTE)) {
                throw new OutroContatoTipoGerenciaJaCadastradoException();
            }
        }

        outroContato = gateway.consultarPorTelefone(novoOutroContato.getTelefone());

        if(outroContato.isPresent()) {
            throw new OutroContatoComMesmoTelefoneJaCadastradoExcetion();
        }

        Usuario usuario = usuarioUseCase.consultarPorId(novoOutroContato.getUsuario().getId());

        novoOutroContato.setUsuario(usuario);

        return gateway.salvar(novoOutroContato);
    }

    public Page<OutroContato> listar(Pageable pageable, UUID idUsuario) {
        return gateway.listar(pageable, idUsuario);
    }

    public OutroContato alterar(Long idOutroContato, OutroContato novosDados) {
        Optional<OutroContato> outroContato = gateway.consultarPorTipo(novosDados.getTipoContato(), novosDados.getUsuario().getId());

        if(outroContato.isPresent()) {
            if(outroContato.get().getTipoContato().equals(TipoContato.GERENTE)) {
                throw new OutroContatoTipoGerenciaJaCadastradoException();
            }
        }

        outroContato = gateway.consultarPorTelefone(novosDados.getTelefone());

        if(outroContato.isPresent()) {
            throw new OutroContatoComMesmoTelefoneJaCadastradoExcetion();
        }

        OutroContato outroContatoExistente = this.consultarPorId(idOutroContato);

        outroContatoExistente.setDados(novosDados);

        return gateway.salvar(outroContatoExistente);
    }

    private OutroContato consultarPorId(Long idOutroContato) {
        Optional<OutroContato> outroContato = gateway.consultarPorId(idOutroContato);

        if(outroContato.isEmpty()) {
            throw new OutroContatoNaoEncontradoException();
        }

        return outroContato.get();
    }

    public void deletar(Long id) {
        this.consultarPorId(id);
        gateway.deletar(id);
    }
}
