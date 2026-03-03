package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.LimiteDeUmBaseConhecimentoJaAtingidoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.BaseConhecimentoNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.BaseConhecimentoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.BaseConhecimento;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaseConhecimentoUseCase {

    private final BaseConhecimentoGateway gateway;
    private final UsuarioUseCase usuarioUseCase;

    public BaseConhecimento cadastrar(BaseConhecimento novoBaseConhecimento) {
        Usuario usuario = usuarioUseCase.consultarPorId(novoBaseConhecimento.getUsuario().getId());

        novoBaseConhecimento.setUsuario(usuario);

        List<BaseConhecimento> BaseConhecimentos = this.listar(usuario.getId());

        if(BaseConhecimentos.size() > 1) {
            throw new LimiteDeUmBaseConhecimentoJaAtingidoException();
        }

        return gateway.salvar(novoBaseConhecimento);
    }

    public List<BaseConhecimento> listar(UUID idUsuario) {
        return gateway.listar(idUsuario);
    }

    public BaseConhecimento alterar(UUID idBaseConhecimento, BaseConhecimento BaseConhecimento) {
        BaseConhecimento BaseConhecimentoExistente = this.consultarPorId(idBaseConhecimento);

        BaseConhecimentoExistente.setDados(BaseConhecimento);

        return gateway.salvar(BaseConhecimento);
    }


    public void deletar(UUID idBaseConhecimento) {
        this.consultarPorId(idBaseConhecimento);
        gateway.deletar(idBaseConhecimento);
    }

    private BaseConhecimento consultarPorId(UUID idBaseConhecimento) {
        Optional<BaseConhecimento> BaseConhecimento = gateway.consultarPorId(idBaseConhecimento);

        if(BaseConhecimento.isEmpty()) {
            throw new BaseConhecimentoNaoEncontradoException();
        }

        return BaseConhecimento.get();
    }
}
