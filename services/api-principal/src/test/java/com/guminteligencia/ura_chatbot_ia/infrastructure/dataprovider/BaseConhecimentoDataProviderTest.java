package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import static org.junit.jupiter.api.Assertions.*;

import com.guminteligencia.ura_chatbot_ia.domain.BaseConhecimento;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.BaseConhecimentoMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.BaseConhecimentoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.BaseConhecimentoEntity;
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
class BaseConhecimentoDataProviderTest {

    @Mock
    private BaseConhecimentoRepository repository;

    @InjectMocks
    private BaseConhecimentoDataProvider provider;

    private BaseConhecimento domainIn;
    private BaseConhecimento domainOut;
    private BaseConhecimentoEntity entityIn;
    private BaseConhecimentoEntity entityOut;
    private UUID idBaseConhecimento;
    private UUID idUsuario;

    @BeforeEach
    void setup() {
        domainIn = mock(BaseConhecimento.class);
        domainOut = mock(BaseConhecimento.class);
        entityIn = mock(BaseConhecimentoEntity.class);
        entityOut = mock(BaseConhecimentoEntity.class);
        idBaseConhecimento = UUID.randomUUID();
        idUsuario = UUID.randomUUID();
    }

    // =========================================================================
    // TESTES DO MÉTODO SALVAR
    // =========================================================================

    @Test
    @DisplayName("Deve salvar a base de conhecimento com sucesso")
    void deveSalvarComSucesso() {
        try (MockedStatic<BaseConhecimentoMapper> ms = mockStatic(BaseConhecimentoMapper.class)) {
            ms.when(() -> BaseConhecimentoMapper.paraEntity(domainIn)).thenReturn(entityIn);
            when(repository.save(entityIn)).thenReturn(entityOut);
            ms.when(() -> BaseConhecimentoMapper.paraDomain(entityOut)).thenReturn(domainOut);

            BaseConhecimento result = provider.salvar(domainIn);

            assertSame(domainOut, result);
            verify(repository).save(entityIn);
            ms.verify(() -> BaseConhecimentoMapper.paraEntity(domainIn));
            ms.verify(() -> BaseConhecimentoMapper.paraDomain(entityOut));
        }
    }

    @Test
    @DisplayName("Deve lançar DataProviderException ao falhar no salvamento")
    void deveLancarExceptionAoSalvar() {
        try (MockedStatic<BaseConhecimentoMapper> ms = mockStatic(BaseConhecimentoMapper.class)) {
            ms.when(() -> BaseConhecimentoMapper.paraEntity(domainIn)).thenReturn(entityIn);
            when(repository.save(entityIn)).thenThrow(new RuntimeException("fail-save"));

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.salvar(domainIn)
            );

            assertEquals(provider.MENSAGEM_ERRO_SALVAR_BaseConhecimento, ex.getMessage());
            verify(repository).save(entityIn);
        }
    }

    // =========================================================================
    // TESTES DO MÉTODO LISTAR POR USUÁRIO
    // =========================================================================

    @Test
    @DisplayName("Deve listar as bases de conhecimento do usuário com sucesso")
    void deveListarPorUsuarioComSucesso() {
        List<BaseConhecimentoEntity> entities = List.of(entityIn);
        List<BaseConhecimento> expectedDomains = List.of(domainOut);

        when(repository.findByUsuario_Id(idUsuario)).thenReturn(entities);

        try (MockedStatic<BaseConhecimentoMapper> ms = mockStatic(BaseConhecimentoMapper.class)) {
            ms.when(() -> BaseConhecimentoMapper.paraDomain(entityIn)).thenReturn(domainOut);

            List<BaseConhecimento> result = provider.listar(idUsuario);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedDomains, result);

            verify(repository).findByUsuario_Id(idUsuario);
            ms.verify(() -> BaseConhecimentoMapper.paraDomain(entityIn));
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
    @DisplayName("Deve consultar base de conhecimento por ID com sucesso")
    void deveConsultarPorIdComSucesso() {
        when(repository.findById(idBaseConhecimento)).thenReturn(Optional.of(entityIn));

        try (MockedStatic<BaseConhecimentoMapper> ms = mockStatic(BaseConhecimentoMapper.class)) {
            ms.when(() -> BaseConhecimentoMapper.paraDomain(entityIn)).thenReturn(domainOut);

            Optional<BaseConhecimento> result = provider.consultarPorId(idBaseConhecimento);

            assertTrue(result.isPresent());
            assertSame(domainOut, result.get());

            verify(repository).findById(idBaseConhecimento);
            ms.verify(() -> BaseConhecimentoMapper.paraDomain(entityIn));
        }
    }

    @Test
    @DisplayName("Deve retornar vazio quando consultar por ID e não encontrar")
    void deveRetornarVazioAoConsultarPorId() {
        when(repository.findById(idBaseConhecimento)).thenReturn(Optional.empty());

        Optional<BaseConhecimento> result = provider.consultarPorId(idBaseConhecimento);

        assertTrue(result.isEmpty());
        verify(repository).findById(idBaseConhecimento);
    }

    @Test
    @DisplayName("Deve lançar DataProviderException ao falhar na consulta por ID")
    void deveLancarExceptionAoConsultarPorId() {
        when(repository.findById(any(UUID.class))).thenThrow(new RuntimeException("fail-id"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorId(idBaseConhecimento)
        );

        assertEquals(provider.MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getMessage());
        verify(repository).findById(idBaseConhecimento);
    }

    // =========================================================================
    // TESTES DO MÉTODO DELETAR
    // =========================================================================

    @Test
    @DisplayName("Deve deletar base de conhecimento com sucesso")
    void deveDeletarComSucesso() {
        doNothing().when(repository).deleteById(idBaseConhecimento);

        assertDoesNotThrow(() -> provider.deletar(idBaseConhecimento));

        verify(repository).deleteById(idBaseConhecimento);
    }

    @Test
    @DisplayName("Deve lançar DataProviderException ao falhar ao deletar")
    void deveLancarExceptionAoDeletar() {
        doThrow(new RuntimeException("fail-delete")).when(repository).deleteById(idBaseConhecimento);

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.deletar(idBaseConhecimento)
        );

        assertEquals(provider.MENSAGEM_ERRO_DELETAR_POR_ID, ex.getMessage());
        verify(repository).deleteById(idBaseConhecimento);
    }
}