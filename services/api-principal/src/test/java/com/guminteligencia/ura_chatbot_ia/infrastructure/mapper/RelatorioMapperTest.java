package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ObjetoRelatorioDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.objetoRelatorio.RelatorioProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RelatorioMapperTest {

    @Test
    @DisplayName("Deve mapear projeção para DTO corretamente com JSON válido")
    void deveMapearComJsonValido() {
        // Arrange
        RelatorioProjection projection = mock(RelatorioProjection.class);
        LocalDateTime agora = LocalDateTime.now();
        String jsonValido = "{\"segmento\": \"Varejo\", \"prioridade\": \"Alta\"}";

        when(projection.getNome()).thenReturn("João Silva");
        when(projection.getTelefone()).thenReturn("11999999999");
        when(projection.getAtributosQualificacao()).thenReturn(jsonValido);
        when(projection.getNomeVendedor()).thenReturn("Vendedor A");
        when(projection.getDataCriacao()).thenReturn(agora);

        // Act
        List<ObjetoRelatorioDto> result = RelatorioMapper.paraDto(List.of(projection));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        ObjetoRelatorioDto dto = result.get(0);
        assertEquals("João Silva", dto.getNome());
        assertEquals("11999999999", dto.getTelefone());
        assertEquals("Vendedor A", dto.getNome_vendedor());
        assertEquals(agora, dto.getData_criacao());

        // Validação do Map (JSON parseado)
        Map<String, String> mapQualificacao = dto.getAtributos_qualificacao();
        assertNotNull(mapQualificacao);
        assertEquals(2, mapQualificacao.size());
        assertEquals("Varejo", mapQualificacao.get("segmento"));
        assertEquals("Alta", mapQualificacao.get("prioridade"));
    }

    @Test
    @DisplayName("Deve retornar Map vazio quando JSON for nulo ou vazio")
    void deveRetornarMapVazioQuandoJsonNuloOuVazio() {
        // Arrange
        RelatorioProjection projNulo = mock(RelatorioProjection.class);
        when(projNulo.getAtributosQualificacao()).thenReturn(null);

        RelatorioProjection projVazio = mock(RelatorioProjection.class);
        when(projVazio.getAtributosQualificacao()).thenReturn("");

        RelatorioProjection projBlank = mock(RelatorioProjection.class);
        when(projBlank.getAtributosQualificacao()).thenReturn("   ");

        // Act
        List<ObjetoRelatorioDto> result = RelatorioMapper.paraDto(List.of(projNulo, projVazio, projBlank));

        // Assert
        assertEquals(3, result.size());

        // Verifica se todos retornaram map vazio (e não null)
        assertTrue(result.get(0).getAtributos_qualificacao().isEmpty());
        assertTrue(result.get(1).getAtributos_qualificacao().isEmpty());
        assertTrue(result.get(2).getAtributos_qualificacao().isEmpty());
    }

    @Test
    @DisplayName("Deve tratar JsonProcessingException e retornar Map vazio quando JSON for inválido")
    void deveTratarJsonInvalido() {
        // Arrange
        RelatorioProjection projection = mock(RelatorioProjection.class);
        // JSON quebrado (faltando aspas e chaves)
        String jsonInvalido = "{segmento: Varejo";

        when(projection.getAtributosQualificacao()).thenReturn(jsonInvalido);
        // Configura outros campos para não dar NPE se o mapper acessar
        when(projection.getNome()).thenReturn("Cliente Erro");

        // Act
        List<ObjetoRelatorioDto> result = RelatorioMapper.paraDto(List.of(projection));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        // O método deve capturar a exception no log e retornar map vazio
        Map<String, String> map = result.get(0).getAtributos_qualificacao();
        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando input for vazio")
    void deveRetornarListaVazia() {
        // Act
        List<ObjetoRelatorioDto> result = RelatorioMapper.paraDto(Collections.emptyList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
