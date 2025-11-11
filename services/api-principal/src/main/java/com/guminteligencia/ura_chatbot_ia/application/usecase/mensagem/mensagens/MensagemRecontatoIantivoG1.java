package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

@Component
public class MensagemRecontatoIantivoG1 implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {

        StringBuilder mensagem = new StringBuilder();

        if(cliente.getNome() == null && cliente.getDorDesejoPaciente() == null) {
            mensagem.append("Olá, tudo bem? 😊").append("\n");
            mensagem.append("Sou a Luiza, assistente do Dr. Felipe.").append("\n");
            mensagem.append("Notei que paramos nossa conversa. Para darmos sequência à sua triagem, preciso apenas que você me diga seu nome e o procedimento que te interessa.")
                    .append("\n");
            mensagem.append("Assim, garantimos que você não perca tempo para iniciar o atendimento.");

        } else {
            mensagem.append(cliente.getNome())
                    .append(" notei que paramos nossa conversa e quis retornar para que você não perca o seu lugar na fila de agendamento.")
                    .append("\n");

            mensagem.append("O seu objetivo (")
                    .append(cliente.getDorDesejoPaciente())
                    .append(") já estava quase sendo encaminhado para a avaliação de 1 hora com o Dr. Felipe.")
                    .append("\n");

            mensagem.append("Podemos dar sequência agora? A agenda do Dr. Felipe está com poucos horários disponíveis, e gostaria de garantir que você de início ao seu tratamento! 😊");
        }
        
        return mensagem.toString();
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.RECONTATO_INATIVO_G1.getCodigo();
    }
}
