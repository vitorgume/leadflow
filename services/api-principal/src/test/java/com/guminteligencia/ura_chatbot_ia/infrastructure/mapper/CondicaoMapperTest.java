package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConectorLogico;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.CondicaoEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CondicaoMapperTest {

    private Condicao condicaoDomain;
    private CondicaoEntity condicaoEntity;

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
        condicaoEntity = CondicaoEntity.builder()
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
        Condicao result = CondicaoMapper.paraDomain(condicaoEntity);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(condicaoEntity.getId(), result.getId());
        Assertions.assertEquals(condicaoEntity.getCampo(), result.getCampo());
        Assertions.assertEquals(condicaoEntity.getOperadorLogico(), result.getOperadorLogico());
        Assertions.assertEquals(condicaoEntity.getValor(), result.getValor());
        Assertions.assertEquals(condicaoEntity.getConectorLogico(), result.getConectorLogico());
    }

    @Test
    @DisplayName("Deve transformar Domain em Entity corretamente")
    void deveTransformarParaEntity() {
        // Act
        CondicaoEntity result = CondicaoMapper.paraEntity(condicaoDomain);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(condicaoDomain.getId(), result.getId());
        Assertions.assertEquals(condicaoDomain.getCampo(), result.getCampo());
        Assertions.assertEquals(condicaoDomain.getOperadorLogico(), result.getOperadorLogico());
        Assertions.assertEquals(condicaoDomain.getValor(), result.getValor());
        Assertions.assertEquals(condicaoDomain.getConectorLogico(), result.getConectorLogico());
    }

}