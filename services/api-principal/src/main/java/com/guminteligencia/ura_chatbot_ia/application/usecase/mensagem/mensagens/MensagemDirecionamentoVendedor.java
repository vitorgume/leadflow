package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.UsuarioUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MensagemDirecionamentoVendedor implements MensagemType {

    private final UsuarioUseCase usuarioUseCase;

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {

        String mensagem = cliente.getUsuario().getMensagemDirecionamentoVendedor();

        mensagem = mensagem.replaceAll("\\{nome_cliente\\}", cliente.getNome());

        for (Map.Entry<String, Object> dado : cliente.getAtributosQualificacao().entrySet()) {
            mensagem = mensagem.replaceAll("\\{" + dado.getKey() + "\\}", dado.getValue().toString());
        }

        return mensagem;
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR.getCodigo();
    }
}
