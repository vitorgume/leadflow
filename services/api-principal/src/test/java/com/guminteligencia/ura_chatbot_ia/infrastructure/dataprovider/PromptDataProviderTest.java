package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import static org.junit.jupiter.api.Assertions.*;

import com.guminteligencia.ura_chatbot_ia.domain.Prompt;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.PromptMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.PromptRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.PromptEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PromptDataProviderTest {

    @Mock
    private PromptRepository repository;

    @InjectMocks
    private PromptDataProvider provider;

    private Prompt domainIn;
    private Prompt domainOut;
    private PromptEntity entityIn;
    private PromptEntity entityOut;
    private UUID idPrompt;
    private UUID idUsuario;

    @BeforeEach
    void setup() {
        domainIn = mock(Prompt.class);
        domainOut = mock(Prompt.class);
        entityIn = mock(PromptEntity.class);
        entityOut = mock(PromptEntity.class);
        idPrompt = UUID.randomUUID();
        idUsuario = UUID.randomUUID();
    }

    // =========================================================================
    // TESTES DO MÉTODO SALVAR
    // =========================================================================

    @Test
    @DisplayName("Deve salvar o prompt com sucesso")
    void deveSalvarComSucesso() {
        try (MockedStatic<PromptMapper> ms = mockStatic(PromptMapper.class)) {
            ms.when(() -> PromptMapper.paraEntity(domainIn)).thenReturn(entityIn);
            when(repository.save(entityIn)).thenReturn(entityOut);
            ms.when(() -> PromptMapper.paraDomain(entityOut)).thenReturn(domainOut);

            Prompt result = provider.salvar(domainIn);

            assertSame(domainOut, result);
            verify(repository).save(entityIn);
            ms.verify(() -> PromptMapper.paraEntity(domainIn));
            ms.verify(() -> PromptMapper.paraDomain(entityOut));
        }
    }

    @Test
    @DisplayName("Deve lançar DataProviderException ao falhar no salvamento")
    void deveLancarExceptionAoSalvar() {
        try (MockedStatic<PromptMapper> ms = mockStatic(PromptMapper.class)) {
            ms.when(() -> PromptMapper.paraEntity(domainIn)).thenReturn(entityIn);
            when(repository.save(entityIn)).thenThrow(new RuntimeException("fail-save"));

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.salvar(domainIn)
            );

            assertEquals(provider.MENSAGEM_ERRO_SALVAR_PROMPT, ex.getMessage());
            verify(repository).save(entityIn);
        }
    }

    // =========================================================================
    // TESTES DO MÉTODO LISTAR POR USUÁRIO
    // =========================================================================

    @Test
    @DisplayName("Deve listar os prompts do usuário com sucesso")
    void deveListarPorUsuarioComSucesso() {
        List<PromptEntity> entities = List.of(entityIn);
        List<Prompt> expectedDomains = List.of(domainOut);

        when(repository.findByUsuario_Id(idUsuario)).thenReturn(entities);

        try (MockedStatic<PromptMapper> ms = mockStatic(PromptMapper.class)) {
            ms.when(() -> PromptMapper.paraDomain(entityIn)).thenReturn(domainOut);

            List<Prompt> result = provider.listar(idUsuario);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedDomains, result);

            verify(repository).findByUsuario_Id(idUsuario);
            ms.verify(() -> PromptMapper.paraDomain(entityIn));
        }
    }

    @Test
    @DisplayName("Deve lançar DataProviderException ao falhar na listagem")
    void deveLancarExceptionAoListar() {
        when(repository.findByUsuario_Id(any(UUID.class))).thenThrow(new RuntimeException("fail-list"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.listar(idUsuario)
        );

        assertEquals(provider.MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex.getMessage());
        verify(repository).findByUsuario_Id(idUsuario);
    }

    // =========================================================================
    // TESTES DO MÉTODO CONSULTAR POR ID
    // =========================================================================

    @Test
    @DisplayName("Deve consultar prompt por ID com sucesso")
    void deveConsultarPorIdComSucesso() {
        when(repository.findById(idPrompt)).thenReturn(Optional.of(entityIn));

        try (MockedStatic<PromptMapper> ms = mockStatic(PromptMapper.class)) {
            ms.when(() -> PromptMapper.paraDomain(entityIn)).thenReturn(domainOut);

            Optional<Prompt> result = provider.consultarPorId(idPrompt);

            assertTrue(result.isPresent());
            assertSame(domainOut, result.get());

            verify(repository).findById(idPrompt);
            ms.verify(() -> PromptMapper.paraDomain(entityIn));
        }
    }

    @Test
    @DisplayName("Deve retornar vazio quando consultar por ID e não encontrar")
    void deveRetornarVazioAoConsultarPorId() {
        when(repository.findById(idPrompt)).thenReturn(Optional.empty());

        Optional<Prompt> result = provider.consultarPorId(idPrompt);

        assertTrue(result.isEmpty());
        verify(repository).findById(idPrompt);
    }

    @Test
    @DisplayName("Deve lançar DataProviderException ao falhar na consulta por ID")
    void deveLancarExceptionAoConsultarPorId() {
        when(repository.findById(any(UUID.class))).thenThrow(new RuntimeException("fail-id"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorId(idPrompt)
        );

        assertEquals(provider.MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getMessage());
        verify(repository).findById(idPrompt);
    }

    // =========================================================================
    // TESTES DO MÉTODO DELETAR
    // =========================================================================

    @Test
    @DisplayName("Deve deletar prompt com sucesso")
    void deveDeletarComSucesso() {
        doNothing().when(repository).deleteById(idPrompt);

        assertDoesNotThrow(() -> provider.deletar(idPrompt));

        verify(repository).deleteById(idPrompt);
    }

    @Test
    @DisplayName("Deve lançar DataProviderException ao falhar ao deletar")
    void deveLancarExceptionAoDeletar() {
        doThrow(new RuntimeException("fail-delete")).when(repository).deleteById(idPrompt);

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.deletar(idPrompt)
        );

        assertEquals(provider.MENSAGEM_ERRO_DELETAR_POR_ID, ex.getMessage());
        verify(repository).deleteById(idPrompt);
    }
}