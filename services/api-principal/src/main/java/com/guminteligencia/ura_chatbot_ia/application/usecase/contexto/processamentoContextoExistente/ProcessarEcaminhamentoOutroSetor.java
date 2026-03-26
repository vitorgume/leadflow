package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.AgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.SetorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Setor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class ProcessarEcaminhamentoOutroSetor implements ProcessamentoContextoExistenteType {

    private final AgenteUseCase agenteUseCase;
    private final SetorUseCase setorUseCase;
    private final Random random = new Random();


    @Override
    public void processar(String resposta, ConversaAgente conversaAgente, Cliente cliente) {
        String nomeSetor = agenteUseCase.enviarOutroContatoTransformacao(resposta, cliente.getUsuario().getId());

        Setor setor = setorUseCase.consultarPorNome(nomeSetor);

        int quantidadeMembros = setor.getMembros().size();
        int posicaoMembroEscolhido;

        if(quantidadeMembros > 1) {
            posicaoMembroEscolhido = random.nextInt(quantidadeMembros);
        } else {
            posicaoMembroEscolhido = quantidadeMembros;
        }

        Membro membroSetorEscolhido = setor.getMembros().get(posicaoMembroEscolhido);
    }

    @Override
    public boolean deveProcessar(String resposta, ConversaAgente conversaAgente) {
        return isQualificado(resposta);
    }

    private boolean isQualificado(String resposta) {
        if (resposta == null) return false;

        String textoNormalizado = resposta
                .toLowerCase()
                .replaceAll("\\s+", "");

        return textoNormalizado.contains("outro_setor:true");
    }
}
