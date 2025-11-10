package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MensagemDadosContatoAtendente implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        StringBuilder mensagem = new StringBuilder();
        LocalDateTime dataHoje = LocalDateTime.now();

        String horaMinutos = String.format("%02d:%02d", dataHoje.getHour(), dataHoje.getMinute());

        mensagem.append("Dados do contato acima:\n");

        if(cliente.getNome() != null) {
            mensagem.append("Nome: ").append(cliente.getNome()).append("\n");
        } else {
            mensagem.append("Nome: ").append("Nome não informado").append("\n");
        }

        if(cliente.getCpf() != null) {
            mensagem.append("Cpf: ").append(cliente.getCpf()).append("\n");
        } else {
            mensagem.append("Cpf: ").append("Cpf não informado").append("\n");
        }

        if(cliente.getConsentimentoAtendimnento() != null) {
            String resposta = cliente.getConsentimentoAtendimnento() ? "Está consciente" : "Não está consentido";

            mensagem.append("Consentimento Atendimento: ").append(resposta).append("\n");
        } else {
            mensagem.append("Consentimento Atendimento: ").append("Consentimento atendimento não informado").append("\n");
        }

        if(cliente.getTipoConsulta() != null) {
            mensagem.append("Tipo da Consutla: ").append(cliente.getTipoConsulta().getDescricao()).append("\n");
        } else {
            mensagem.append("Tipo da Consulta: ").append("Tipo da consulta não informado.").append("\n");
        }

        if(cliente.getDorDesejoPaciente() != null) {
            mensagem.append("Dor do Paciente: ").append(cliente.getDorDesejoPaciente()).append("\n");
        } else {
            mensagem.append("Dor do Paciente: ").append("Dor do paciente não informada").append("\n");
        }

        if(cliente.getLinkMidia() != null) {
            mensagem.append("Link da Mídia: ").append(cliente.getLinkMidia()).append("\n");
        } else {
            mensagem.append("Link da Mídia: ").append("Link da mídia do usuário não recebida.").append("\n");
        }

        if(cliente.getPreferenciaHorario() != null) {
            mensagem.append("Preferência de Horário: ").append(cliente.getPreferenciaHorario().getDescricao()).append("\n");
        } else {
            mensagem.append("Preferência de Horário: ").append("Preferência de horário não informada").append("\n");
        }

        if(cliente.getTelefone() != null) {
            mensagem.append("Telefone: ").append(cliente.getTelefone()).append("\n");
        } else {
            mensagem.append("Telefone: ").append("Telefone não informado").append("\n");
        }

        mensagem.append("Hora: ").append(horaMinutos).append("\n");

        return mensagem.toString();
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.DADOS_CONTATO_VENDEDOR.getCodigo();
    }
}
