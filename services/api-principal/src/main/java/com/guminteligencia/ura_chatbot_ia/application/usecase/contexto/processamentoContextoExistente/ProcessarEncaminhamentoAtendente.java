package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Order(3)
public class ProcessarEncaminhamentoAtendente implements ProcessamentoContextoExistenteType {

    private final MensagemUseCase mensagemUseCase;
    private final MensagemBuilder mensagemBuilder;

    @Override
    public void processar(String resposta, ConversaAgente conversaAgente, Cliente cliente) {
        mensagemUseCase.enviarContato(conversaAgente.getVendedor(), cliente);
        mensagemUseCase.enviarMensagem(mensagemBuilder.getMensagem(TipoMensagem.REDIRECIONAMENTO_RECONTATO, null, null), cliente.getTelefone(), false);
    }

    @Override
    public boolean deveProcessar(String resposta, ConversaAgente conversaAgente) {
        if (resposta == null) return false;

        String textoNormalizado = resposta
                .toLowerCase()
                .replaceAll("\\s+", "");

        return textoNormalizado.contains("encaminhar:true");
    }
}
