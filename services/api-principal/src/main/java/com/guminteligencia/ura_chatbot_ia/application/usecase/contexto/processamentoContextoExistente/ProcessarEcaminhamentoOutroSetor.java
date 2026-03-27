package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.AgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.SetorUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.CrmUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Setor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessarEcaminhamentoOutroSetor implements ProcessamentoContextoExistenteType {

    private final AgenteUseCase agenteUseCase;
    private final SetorUseCase setorUseCase;
    private final Random random = new Random();
    private final MensagemUseCase mensagemUseCase;
    private final CrmUseCase crmUseCase;
    private final MensagemBuilder mensagemBuilder;


    @Override
    public void processar(String resposta, ConversaAgente conversaAgente, Cliente cliente) {
        log.info("Processando direcionameneto para outro setor. Resposta: {}, Conersa: {}, Cliente: {}", resposta, conversaAgente, cliente);

        String nomeSetor = agenteUseCase.enviarOutroContatoTransformacao(resposta, cliente.getUsuario().getId());

        Setor setor = setorUseCase.consultarPorNome(nomeSetor, cliente.getUsuario().getId());

        int quantidadeMembros = setor.getMembros().size();
        int posicaoMembroEscolhido;

        if(quantidadeMembros > 1) {
            posicaoMembroEscolhido = random.nextInt(quantidadeMembros);
        } else {
            posicaoMembroEscolhido = quantidadeMembros;
        }

        Membro membroSetorEscolhido = setor.getMembros().get(posicaoMembroEscolhido);

        mensagemUseCase.enviarMensagem(
                mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_DIRECIONAMENTO_OUTRO_SETOR, membroSetorEscolhido.getNome(), cliente),
                cliente.getTelefone(),
                false,
                cliente.getUsuario()
        );

        mensagemUseCase.enviarContato(membroSetorEscolhido.getTelefone(), cliente);

        conversaAgente.setMembro(membroSetorEscolhido);
        conversaAgente.setFinalizada(true);
        log.info("Precessamento de direcionamento para outro setor concluido com sucesso.");
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
