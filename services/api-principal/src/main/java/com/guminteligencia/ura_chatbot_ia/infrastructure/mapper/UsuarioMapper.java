package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;

public class UsuarioMapper {

    public static Usuario paraDomain(UsuarioEntity entity) {
        return Usuario.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .telefone(entity.getTelefone())
                .senha(entity.getSenha())
                .email(entity.getEmail())
                .telefoneConcectado(entity.getTelefoneConectado())
                .atributosQualificacao(entity.getAtributosQualificacao())
                .configuracaoCrm(ConfiguracaoCrmMapper.paraDomain(entity.getConfiguracaoCrm()))
                .mensagemDirecionamentoVendedor(entity.getMensagemDirecionamentoVendedor())
                .mensagemRecontatoG1(entity.getMensagemRecontatoG1())
                .whatsappToken(entity.getWhatsappToken())
                .whatsappIdInstance(entity.getWhatsappIdInstance())
                .agenteApiKey(entity.getAgenteApiKey())
                .build();
    }

    public static UsuarioEntity paraEntity(Usuario domain) {
        return UsuarioEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .senha(domain.getSenha())
                .email(domain.getEmail())
                .telefoneConectado(domain.getTelefoneConcectado())
                .atributosQualificacao(domain.getAtributosQualificacao())
                .configuracaoCrm(ConfiguracaoCrmMapper.paraEntity(domain.getConfiguracaoCrm()))
                .mensagemDirecionamentoVendedor(domain.getMensagemDirecionamentoVendedor())
                .mensagemRecontatoG1(domain.getMensagemRecontatoG1())
                .whatsappToken(domain.getWhatsappToken())
                .whatsappIdInstance(domain.getWhatsappIdInstance())
                .agenteApiKey(domain.getAgenteApiKey())
                .build();
    }
}
