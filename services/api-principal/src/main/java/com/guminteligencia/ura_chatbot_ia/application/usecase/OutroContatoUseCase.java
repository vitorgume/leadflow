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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutroContatoUseCase {

    private final OutroContatoGateway gateway;
    private final UsuarioUseCase usuarioUseCase;
    private final OutroContatoNoSqlUseCase outroContatoNoSqlUseCase;

    public OutroContato consultarPorNome(String nome) {
        Optional<OutroContato> outroContato = gateway.consultarPorNome(nome);

        if (outroContato.isEmpty()) {
            throw new OutroContatoNaoEncontradoException();
        }

        return outroContato.get();
    }

    public List<OutroContato> consultarPorTipo(TipoContato tipo, UUID idUsuario) {
        return gateway.consultarPorTipo(tipo, idUsuario);
    }

    public OutroContato cadastrar(OutroContato novoOutroContato) {
        List<OutroContato> outroContatos = gateway.consultarPorTipo(novoOutroContato.getTipoContato(), novoOutroContato.getUsuario().getId());

        if (outroContatos.size() > 1 && novoOutroContato.getTipoContato().equals(TipoContato.GERENTE)) {
            throw new OutroContatoTipoGerenciaJaCadastradoException();
        }

        Optional<OutroContato> outroContato = gateway.consultarPorTelefoneEUsuario(novoOutroContato.getTelefone(), novoOutroContato.getUsuario().getId());

        if (outroContato.isPresent()) {
            throw new OutroContatoComMesmoTelefoneJaCadastradoExcetion();
        }

        Usuario usuario = usuarioUseCase.consultarPorId(novoOutroContato.getUsuario().getId());

        novoOutroContato.setUsuario(usuario);

        OutroContato outroContatoSalvo = gateway.salvar(novoOutroContato);
        outroContatoNoSqlUseCase.salvar(novoOutroContato);

        return outroContatoSalvo;
    }

    public Page<OutroContato> listar(Pageable pageable, UUID idUsuario) {
        return gateway.listar(pageable, idUsuario);
    }

    public OutroContato alterar(Long idOutroContato, OutroContato novosDados) {
        List<OutroContato> outroContatos = gateway.consultarPorTipo(novosDados.getTipoContato(), novosDados.getUsuario().getId());

        if (novosDados.getTipoContato().equals(TipoContato.GERENTE)) {
            if (outroContatos.stream().anyMatch(
                    outroContato -> outroContato.getTipoContato().equals(TipoContato.GERENTE)
                            && !outroContato.getId().equals(idOutroContato)
                    )
            ) {
                throw new OutroContatoTipoGerenciaJaCadastradoException();
            }
        }

        Optional<OutroContato> outroContato = gateway.consultarPorTelefoneEUsuario(novosDados.getTelefone(), novosDados.getUsuario().getId());


        if (outroContato.isPresent()) {
            if (!outroContato.get().getId().equals(idOutroContato))
                throw new OutroContatoComMesmoTelefoneJaCadastradoExcetion();
        }

        OutroContato outroContatoExistente = this.consultarPorId(idOutroContato);
        OutroContato outroContatoNoSql = outroContatoNoSqlUseCase.consultarPorTelefoneEUsuario(
                outroContatoExistente.getTelefone(), outroContatoExistente.getUsuario().getId());

        outroContatoExistente.setDados(novosDados);
        outroContatoNoSql.setDados(novosDados);

        OutroContato novoDadosSalvos = gateway.salvar(outroContatoExistente);
        outroContatoNoSqlUseCase.salvar(outroContatoNoSql);

        return novoDadosSalvos;
    }

    private OutroContato consultarPorId(Long idOutroContato) {
        Optional<OutroContato> outroContato = gateway.consultarPorId(idOutroContato);

        if (outroContato.isEmpty()) {
            throw new OutroContatoNaoEncontradoException();
        }

        return outroContato.get();
    }

    public void deletar(Long id) {
        this.consultarPorId(id);
        gateway.deletar(id);
    }
}
