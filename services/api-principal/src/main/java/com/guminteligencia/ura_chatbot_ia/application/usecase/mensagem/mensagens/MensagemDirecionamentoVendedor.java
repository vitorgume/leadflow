package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

@Component
public class MensagemDirecionamentoVendedor implements MensagemType {
    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        StringBuilder mensagem = new StringBuilder();

        mensagem.append(cliente.getNome()).append("! Sua triagem está completa! 😊").append("\n");
        mensagem.append("Seu agendamento será tratado com prioridade pela nossa Consultoria, que já tem seu objetivo principal: ").append(cliente.getDorDesejoPaciente())
                .append("\n");
        mensagem.append("Nossas atendentes estão finalizando o encaixe do seu horário exclusivo de 1 hora com o Dr. Felipe. Em instantes, elas entrarão em contato para confirmar a data e o endereço da clínica.")
                .append("\n");
        mensagem.append("Obrigada pela confiança! Em breve, retornamos com as opções. ✨");

        return mensagem.toString();
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR.getCodigo();
    }
}
