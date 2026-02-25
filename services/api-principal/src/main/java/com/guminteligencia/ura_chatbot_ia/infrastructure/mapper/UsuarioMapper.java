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
                .telefoneConectado(entity.getTelefoneConectado())
                .atributosQualificacao(entity.getAtributosQualificacao() == null ? null : entity.getAtributosQualificacao())
                .configuracaoCrm(ConfiguracaoCrmMapper.paraDomain(entity.getConfiguracaoCrm() == null ? null : entity.getConfiguracaoCrm()))
                .mensagemDirecionamentoVendedor(entity.getMensagemDirecionamentoVendedor() == null ? null : entity.getMensagemDirecionamentoVendedor())
                .mensagemRecontatoG1(entity.getMensagemRecontatoG1() == null ? null : entity.getMensagemRecontatoG1())
                .whatsappToken(entity.getWhatsappToken() == null ? null : entity.getWhatsappToken())
                .whatsappIdInstance(entity.getWhatsappIdInstance() == null ? null : entity.getWhatsappIdInstance())
                .agenteApiKey(entity.getAgenteApiKey() == null ? null : entity.getAgenteApiKey())
                .build();
    }

    public static UsuarioEntity paraEntity(Usuario domain) {
        return UsuarioEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .senha(domain.getSenha())
                .email(domain.getEmail())
                .telefoneConectado(domain.getTelefoneConectado())
                .atributosQualificacao(domain.getAtributosQualificacao() == null ? null : domain.getAtributosQualificacao())
                .configuracaoCrm(ConfiguracaoCrmMapper.paraEntity(domain.getConfiguracaoCrm() == null ? null : domain.getConfiguracaoCrm()))
                .mensagemDirecionamentoVendedor(domain.getMensagemDirecionamentoVendedor() == null ? null : domain.getMensagemDirecionamentoVendedor())
                .mensagemRecontatoG1(domain.getMensagemRecontatoG1() == null ? null : domain.getMensagemRecontatoG1())
                .whatsappToken(domain.getWhatsappToken() == null ? null : domain.getWhatsappToken())
                .whatsappIdInstance(domain.getWhatsappIdInstance() == null ? null : domain.getWhatsappIdInstance())
                .agenteApiKey(domain.getAgenteApiKey() == null ? null : domain.getAgenteApiKey())
                .build();
    }
}
