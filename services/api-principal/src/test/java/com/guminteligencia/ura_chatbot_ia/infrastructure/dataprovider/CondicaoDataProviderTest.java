package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.CondicaoMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.CondicaoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.CondicaoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CondicaoDataProviderTest {

    @Mock
    private CondicaoRepository repository;

    @InjectMocks
    private CondicaoDataProvider provider;

    private final UUID ID = UUID.randomUUID();
    private final String MENSAGEM_SALVAR = "Erro ao salvar condição.";
    private final String MENSAGEM_CONSULTAR = "Erro ao consultar condição por id.";
    private final String MENSAGEM_DELETAR = "Erro ao deletar condição.";

    @Test
    @DisplayName("Salvar: Deve converter, salvar e retornar domínio com sucesso")
    void deveSalvarComSucesso() {
        // Arrange
        Condicao condicaoInput = new Condicao();
        CondicaoEntity entityMock = new CondicaoEntity(); // Simula retorno do mapper
        CondicaoEntity entitySalva = new CondicaoEntity(); // Simula retorno do banco
        Condicao condicaoOutput = new Condicao(); // Simula retorno final

        // Mock do Repositório
        when(repository.save(any(CondicaoEntity.class))).thenReturn(entitySalva);

        // Mock Estático do Mapper
        try (MockedStatic<CondicaoMapper> ms = Mockito.mockStatic(CondicaoMapper.class)) {
            ms.when(() -> CondicaoMapper.paraEntity(any(Condicao.class)))
                    .thenReturn(entityMock);

            ms.when(() -> CondicaoMapper.paraDomain(any(CondicaoEntity.class)))
                    .thenReturn(condicaoOutput);

            // Act
            Condicao resultado = provider.salvar(condicaoInput);

            // Assert
            assertNotNull(resultado);
            assertEquals(condicaoOutput, resultado);

            verify(repository).save(entityMock);

            // Verificações do Mapper
            ms.verify(() -> CondicaoMapper.paraEntity(condicaoInput));
            ms.verify(() -> CondicaoMapper.paraDomain(entitySalva));
        }
    }

    @Test
    @DisplayName("Salvar: Deve lançar DataProviderException em caso de erro")
    void deveLancarExcecaoAoSalvar() {
        // Arrange
        RuntimeException exBanco = new RuntimeException("Erro SQL");

        // Mock do Repositório falhando
        when(repository.save(any())).thenThrow(exBanco);

        try (MockedStatic<CondicaoMapper> ms = Mockito.mockStatic(CondicaoMapper.class)) {
            ms.when(() -> CondicaoMapper.paraEntity(any())).thenReturn(new CondicaoEntity());

            // Act & Assert
            DataProviderException ex = assertThrows(DataProviderException.class,
                    () -> provider.salvar(new Condicao()));

            assertEquals(MENSAGEM_SALVAR, ex.getMessage());
        }
    }

    @Test
    @DisplayName("ConsultarPorId: Deve retornar Condicao quando encontrada")
    void deveConsultarPorIdComSucesso() {
        // Arrange
        CondicaoEntity entityEncontrada = new CondicaoEntity();
        Condicao condicaoEsperada = new Condicao();

        when(repository.findById(ID)).thenReturn(Optional.of(entityEncontrada));

        try (MockedStatic<CondicaoMapper> ms = Mockito.mockStatic(CondicaoMapper.class)) {
            ms.when(() -> CondicaoMapper.paraDomain(any(CondicaoEntity.class)))
                    .thenReturn(condicaoEsperada);

            // Act
            Optional<Condicao> resultado = provider.consultarPorId(ID);

            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(condicaoEsperada, resultado.get());
        }
    }

    @Test
    @DisplayName("ConsultarPorId: Deve retornar vazio quando não encontrar")
    void deveRetornarVazioAoConsultar() {
        // Arrange
        when(repository.findById(ID)).thenReturn(Optional.empty());

        // Act
        Optional<Condicao> resultado = provider.consultarPorId(ID);

        // Assert
        assertTrue(resultado.isEmpty());
        // Mapper não deve ser chamado
    }

    @Test
    @DisplayName("ConsultarPorId: Deve lançar DataProviderException em caso de erro")
    void deveLancarExcecaoAoConsultar() {
        // Arrange
        RuntimeException exBanco = new RuntimeException("Falha na conexão");
        when(repository.findById(ID)).thenThrow(exBanco);

        // Act & Assert
        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.consultarPorId(ID));

        assertEquals(MENSAGEM_CONSULTAR, ex.getMessage());
    }

    @Test
    @DisplayName("Deletar: Deve chamar deleteById com sucesso")
    void deveDeletarComSucesso() {
        // Act
        provider.deletar(ID);

        // Assert
        verify(repository).deleteById(ID);
    }

    @Test
    @DisplayName("Deletar: Deve lançar DataProviderException em caso de erro")
    void deveLancarExcecaoAoDeletar() {
        // Arrange
        RuntimeException exBanco = new RuntimeException("Erro de constraint");
        doThrow(exBanco).when(repository).deleteById(ID);

        // Act & Assert
        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.deletar(ID));

        assertEquals(MENSAGEM_DELETAR, ex.getMessage());
    }

}