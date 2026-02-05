package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConectorLogico;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.CondicaoDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.CondicaoEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class CondicaoMapperTest {
    private Condicao condicaoDomain;
    private CondicaoDto condicaoDto;

    @BeforeEach
    void setUp() {
        // Mock do Domínio
        condicaoDomain = Condicao.builder()
                .id(UUID.randomUUID())
                .campo("faturamento")
                .operadorLogico(OperadorLogico.IS_GREATER_THAN) // Ajuste conforme seus Enums reais
                .valor("10000")
                .conectorLogico(ConectorLogico.AND) // Ajuste conforme seus Enums reais
                .build();

        // Mock da Entidade
        condicaoDto = CondicaoDto.builder()
                .id(UUID.randomUUID())
                .campo("setor")
                .operadorLogico(OperadorLogico.EQUAL) // Ajuste conforme seus Enums reais
                .valor("Tecnologia")
                .conectorLogico(ConectorLogico.OR) // Ajuste conforme seus Enums reais
                .build();
    }

    @Test
    @DisplayName("Deve transformar Entity em Domain corretamente")
    void deveTransformarParaDomain() {
        // Act
        Condicao result = CondicaoMapper.paraDomain(condicaoDto);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(condicaoDto.getId(), result.getId());
        Assertions.assertEquals(condicaoDto.getCampo(), result.getCampo());
        Assertions.assertEquals(condicaoDto.getOperadorLogico(), result.getOperadorLogico());
        Assertions.assertEquals(condicaoDto.getValor(), result.getValor());
        Assertions.assertEquals(condicaoDto.getConectorLogico(), result.getConectorLogico());
    }

    @Test
    @DisplayName("Deve transformar Domain em Entity corretamente")
    void deveTransformarParaEntity() {
        // Act
        CondicaoDto result = CondicaoMapper.paraDto(condicaoDomain);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(condicaoDomain.getId(), result.getId());
        Assertions.assertEquals(condicaoDomain.getCampo(), result.getCampo());
        Assertions.assertEquals(condicaoDomain.getOperadorLogico(), result.getOperadorLogico());
        Assertions.assertEquals(condicaoDomain.getValor(), result.getValor());
        Assertions.assertEquals(condicaoDomain.getConectorLogico(), result.getConectorLogico());
    }
}