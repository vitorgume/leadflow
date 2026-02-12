package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensagemGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.CriptografiaJCAUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MensagemUseCase {

    private final MensagemGateway gateway;
    private final MensagemBuilder mensagemBuilder;
    private final CriptografiaJCAUseCase criptografiaJCAUseCase;

    public void enviarMensagem(String mensagem, String telefone, boolean semEspacos, Usuario usuario) {
        if (mensagem == null) {
            log.warn("Mensagem nula recebida para envio. Telefone: {}", telefone);
            return;
        }

        log.info("Enviando mensagem para usuario. Resposta: {}, Telefone: {}", mensagem, telefone);

        String mensagemAEnviar = mensagem
                .replaceAll("^\"|\"$", "")
                .replace("\\r\\n", "\n")
                .replace("\\n", "\n")
                .replace("\r\n", "\n");

        if (semEspacos) {
            mensagemAEnviar = mensagemAEnviar
                    .replace("\r", " ")
                    .replace("\n", " ")
                    .replaceAll("\\s+", " ")
                    .trim();
        }

        this.gateway.enviar(mensagemAEnviar, telefone, criptografiaJCAUseCase.descriptografar(usuario.getWhatsappIdInstance()), criptografiaJCAUseCase.descriptografar(usuario.getWhatsappToken()));

        log.info("Mensagem para o usuario enviada com sucesso.");
    }

    public void enviarContato(Vendedor vendedor, Cliente cliente) {
        log.info("Enviando contato para vendedor. Vendedor: {}, Cliente: {}", vendedor, cliente);

        String textoMensagem = mensagemBuilder.getMensagem(TipoMensagem.DADOS_CONTATO_VENDEDOR, null, cliente);
        String textoSeparacao = mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_SEPARACAO, null, null);

        try {
            gateway.enviarContato(vendedor.getTelefone(), cliente, criptografiaJCAUseCase.descriptografar(cliente.getUsuario().getWhatsappIdInstance()), criptografiaJCAUseCase.descriptografar(cliente.getUsuario().getWhatsappToken()));
        } catch (Exception e) {
            log.error("Erro ao enviar contato para vendedor", e);
        }

        this.enviarMensagem(textoMensagem, vendedor.getTelefone(), false, cliente.getUsuario());
        this.enviarMensagem(textoSeparacao, vendedor.getTelefone(), false, cliente.getUsuario());

        log.info("Contato enviado com sucesso para vendedor.");
    }

    public void enviarRelatorio(String arquivo, String fileName, String telefone, Usuario usuario) {
        log.info("Enviando relatorio de vendedores.");
        gateway.enviarRelatorio(arquivo, fileName, telefone, criptografiaJCAUseCase.descriptografar(usuario.getWhatsappIdInstance()), criptografiaJCAUseCase.descriptografar(usuario.getWhatsappToken()));
        log.info("Relatorio enviado com sucesso.");
    }
}
