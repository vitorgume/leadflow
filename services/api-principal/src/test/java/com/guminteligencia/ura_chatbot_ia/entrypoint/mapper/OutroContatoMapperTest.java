package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ConfiguracaoCrmDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.OutroContatoDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class OutroContatoMapperTest {

    private OutroContatoDto outroContatoDto;
    private OutroContato outroContatoDomain;

    @BeforeEach
    void setUp() {
        outroContatoDto = OutroContatoDto.builder()
                .id(1L)
                .nome("Nome outro contato")
                .telefone("000000000000")
                .descricao("Descrição domain")
                .usuario(UsuarioDto.builder().id(UUID.randomUUID()).configuracaoCrm(ConfiguracaoCrmDto.builder().crmType(CrmType.KOMMO).build()).build()) // Added dummy UsuarioEntity with ConfiguracaoCrmEntity
                .build();

        outroContatoDomain = OutroContato.builder()
                .id(1L)
                .nome("Nome outro contato")
                .telefone("000000000000")
                .descricao("Descrição domain")
                .usuario(Usuario.builder().id(UUID.randomUUID()).configuracaoCrm(ConfiguracaoCrm.builder().crmType(CrmType.KOMMO).build()).build()) // Added dummy UsuarioEntity with ConfiguracaoCrmEntity
                .build();
    }

    @Test
    void deveTransformaraParaDomain() {
        OutroContato outroContatoTeste = OutroContatoMapper.paraDomain(outroContatoDto);

        Assertions.assertEquals(outroContatoTeste.getId(), outroContatoDto.getId());
        Assertions.assertEquals(outroContatoTeste.getNome(), outroContatoDto.getNome());
        Assertions.assertEquals(outroContatoTeste.getTelefone(), outroContatoDto.getTelefone());
        Assertions.assertEquals(outroContatoTeste.getDescricao(), outroContatoDto.getDescricao());
    }

    @Test
    void deveTransformarParaEntity() {
        OutroContatoDto outroContatoTeste = OutroContatoMapper.paraDto(outroContatoDomain);

        Assertions.assertEquals(outroContatoTeste.getId(), outroContatoDto.getId());
        Assertions.assertEquals(outroContatoTeste.getNome(), outroContatoDto.getNome());
        Assertions.assertEquals(outroContatoTeste.getTelefone(), outroContatoDto.getTelefone());
        Assertions.assertEquals(outroContatoTeste.getDescricao(), outroContatoDto.getDescricao());
    }
}