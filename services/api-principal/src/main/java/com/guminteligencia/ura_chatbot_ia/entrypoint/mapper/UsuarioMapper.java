package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;

public class UsuarioMapper {

    public static UsuarioDto paraDto(Usuario domain) {
        return UsuarioDto.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .senha(domain.getSenha())
                .email(domain.getEmail())
                .telefoneConectado(domain.getTelefoneConectado())
                .atributosQualificacao(domain.getAtributosQualificacao() == null ? null : domain.getAtributosQualificacao())
                .configuracaoCrm(ConfiguracaoCrmMapper.paraDto(domain.getConfiguracaoCrm() == null ? null : domain.getConfiguracaoCrm()))
                .mensagemDirecionamentoVendedor(domain.getMensagemDirecionamentoVendedor() == null ? null : domain.getMensagemDirecionamentoVendedor())
                .mensagemRecontatoG1(domain.getMensagemRecontatoG1() == null ? null : domain.getMensagemRecontatoG1())
                .build();
    }

    public static Usuario paraDomain(UsuarioDto dto) {
        return Usuario.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .senha(dto.getSenha())
                .email(dto.getEmail())
                .telefoneConectado(dto.getTelefoneConectado())
                .atributosQualificacao(dto.getAtributosQualificacao() == null ? null : dto.getAtributosQualificacao())
                .configuracaoCrm(ConfiguracaoCrmMapper.paraDomain(dto.getConfiguracaoCrm() == null ? null : dto.getConfiguracaoCrm()))
                .mensagemDirecionamentoVendedor(dto.getMensagemDirecionamentoVendedor() == null ? null : dto.getMensagemDirecionamentoVendedor())
                .mensagemRecontatoG1(dto.getMensagemRecontatoG1() == null ? null : dto.getMensagemRecontatoG1())
                .whatsappToken(dto.getWhatsappToken() == null ? null : dto.getWhatsappToken())
                .whatsappIdInstance(dto.getWhatsappIdInstance() == null ? null : dto.getWhatsappIdInstance())
                .agenteApiKey(dto.getAgenteApiKey() == null ? null : dto.getAgenteApiKey())
                .build();
    }
}
