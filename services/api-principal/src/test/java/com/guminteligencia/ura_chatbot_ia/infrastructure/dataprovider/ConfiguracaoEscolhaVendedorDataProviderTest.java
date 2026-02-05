package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConfiguracaoEscolhaVendedor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ConfiguracaoEscolhaVendedorMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ConfiguracaoEscolhaVendedorRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.ConfiguracaoEscolhaVendedorEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ConfiguracaoEscolhaVendedorDataProviderTest {

    @Mock
    private ConfiguracaoEscolhaVendedorRepository repository;

    @InjectMocks
    private ConfiguracaoEscolhaVendedorDataProvider provider;

    private final UUID ID = UUID.randomUUID();
    private final UUID ID_USUARIO = UUID.randomUUID();

    // Mensagens de erro esperadas (constantes da classe original)
    private static final String MSG_ERRO_SALVAR = "Erro ao salvar configuração de escolha do vendedor.";
    private static final String MSG_ERRO_LISTAR = "Erro ao listar configurações de escolha do vendedor por usuário.";
    private static final String MSG_ERRO_CONSULTAR = "Erro ao consultar configuração de escolha do vendedor por id.";

    @Test
    @DisplayName("Salvar: Deve converter, salvar e retornar domínio com sucesso")
    void deveSalvarComSucesso() {
        // Arrange
        ConfiguracaoEscolhaVendedor input = new ConfiguracaoEscolhaVendedor();
        ConfiguracaoEscolhaVendedorEntity entityMock = new ConfiguracaoEscolhaVendedorEntity();
        ConfiguracaoEscolhaVendedor output = new ConfiguracaoEscolhaVendedor();

        when(repository.save(any(ConfiguracaoEscolhaVendedorEntity.class))).thenReturn(entityMock);

        try (MockedStatic<ConfiguracaoEscolhaVendedorMapper> ms = Mockito.mockStatic(ConfiguracaoEscolhaVendedorMapper.class)) {
            ms.when(() -> ConfiguracaoEscolhaVendedorMapper.paraEntity(any(ConfiguracaoEscolhaVendedor.class)))
                    .thenReturn(entityMock);
            ms.when(() -> ConfiguracaoEscolhaVendedorMapper.paraDomain(any(ConfiguracaoEscolhaVendedorEntity.class)))
                    .thenReturn(output);

            // Act
            ConfiguracaoEscolhaVendedor result = provider.salvar(input);

            // Assert
            assertNotNull(result);
            assertEquals(output, result);
            verify(repository).save(entityMock);
        }
    }

    @Test
    @DisplayName("Salvar: Deve lançar DataProviderException em caso de erro")
    void deveLancarExcecaoAoSalvar() {
        RuntimeException exBanco = new RuntimeException("Erro SQL");
        when(repository.save(any())).thenThrow(exBanco);

        try (MockedStatic<ConfiguracaoEscolhaVendedorMapper> ms = Mockito.mockStatic(ConfiguracaoEscolhaVendedorMapper.class)) {
            ms.when(() -> ConfiguracaoEscolhaVendedorMapper.paraEntity(any())).thenReturn(new ConfiguracaoEscolhaVendedorEntity());

            DataProviderException ex = assertThrows(DataProviderException.class, () -> provider.salvar(new ConfiguracaoEscolhaVendedor()));

            assertEquals(MSG_ERRO_SALVAR, ex.getMessage());
        }
    }

    @Test
    @DisplayName("ListarPorUsuario: Deve retornar lista mapeada com sucesso")
    void deveListarPorUsuarioComSucesso() {
        ConfiguracaoEscolhaVendedorEntity entity = new ConfiguracaoEscolhaVendedorEntity();
        List<ConfiguracaoEscolhaVendedorEntity> entities = List.of(entity);
        ConfiguracaoEscolhaVendedor domain = new ConfiguracaoEscolhaVendedor();

        when(repository.findByUsuario(ID_USUARIO)).thenReturn(entities);

        try (MockedStatic<ConfiguracaoEscolhaVendedorMapper> ms = Mockito.mockStatic(ConfiguracaoEscolhaVendedorMapper.class)) {
            ms.when(() -> ConfiguracaoEscolhaVendedorMapper.paraDomain(any(ConfiguracaoEscolhaVendedorEntity.class)))
                    .thenReturn(domain);

            // Act
            List<ConfiguracaoEscolhaVendedor> result = provider.listarPorUsuario(ID_USUARIO);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(domain, result.get(0));
            verify(repository).findByUsuario(ID_USUARIO);
        }
    }

    @Test
    @DisplayName("ListarPorUsuario: Deve lançar exceção em caso de erro")
    void deveLancarExcecaoAoListarPorUsuario() {
        RuntimeException exBanco = new RuntimeException("Erro Conexão");
        when(repository.findByUsuario(any())).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class, () -> provider.listarPorUsuario(ID_USUARIO));

        assertEquals(MSG_ERRO_LISTAR, ex.getMessage());
    }

    @Test
    @DisplayName("ListarPorUsuarioPaginado: Deve retornar página mapeada")
    void deveListarPorUsuarioPaginadoComSucesso() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        ConfiguracaoEscolhaVendedorEntity entity = new ConfiguracaoEscolhaVendedorEntity();
        Page<ConfiguracaoEscolhaVendedorEntity> pageEntity = new PageImpl<>(List.of(entity));
        ConfiguracaoEscolhaVendedor domain = new ConfiguracaoEscolhaVendedor();

        when(repository.findByUsuario_Id(ID_USUARIO, pageable)).thenReturn(pageEntity);

        try (MockedStatic<ConfiguracaoEscolhaVendedorMapper> ms = Mockito.mockStatic(ConfiguracaoEscolhaVendedorMapper.class)) {
            ms.when(() -> ConfiguracaoEscolhaVendedorMapper.paraDomain(any(ConfiguracaoEscolhaVendedorEntity.class)))
                    .thenReturn(domain);

            // Act
            Page<ConfiguracaoEscolhaVendedor> result = provider.listarPorUsuarioPaginado(ID_USUARIO, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(domain, result.getContent().get(0));
        }
    }

    @Test
    @DisplayName("ListarPorUsuarioPaginado: Deve lançar exceção em caso de erro")
    void deveLancarExcecaoAoListarPaginado() {
        RuntimeException exBanco = new RuntimeException("Erro Banco");
        when(repository.findByUsuario_Id(any(), any())).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.listarPorUsuarioPaginado(ID_USUARIO, Pageable.unpaged()));

        assertEquals(MSG_ERRO_LISTAR, ex.getMessage());
    }

    @Test
    @DisplayName("ConsultarPorId: Deve retornar Optional com valor")
    void deveConsultarPorIdComSucesso() {
        ConfiguracaoEscolhaVendedorEntity entity = new ConfiguracaoEscolhaVendedorEntity();
        ConfiguracaoEscolhaVendedor domain = new ConfiguracaoEscolhaVendedor();

        when(repository.findById(ID)).thenReturn(Optional.of(entity));

        try (MockedStatic<ConfiguracaoEscolhaVendedorMapper> ms = Mockito.mockStatic(ConfiguracaoEscolhaVendedorMapper.class)) {
            ms.when(() -> ConfiguracaoEscolhaVendedorMapper.paraDomain(any(ConfiguracaoEscolhaVendedorEntity.class)))
                    .thenReturn(domain);

            Optional<ConfiguracaoEscolhaVendedor> result = provider.consultarPorId(ID);

            assertTrue(result.isPresent());
            assertEquals(domain, result.get());
        }
    }

    @Test
    @DisplayName("ConsultarPorId: Deve retornar vazio quando não encontrar")
    void deveRetornarVazioAoConsultarPorId() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        Optional<ConfiguracaoEscolhaVendedor> result = provider.consultarPorId(ID);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("ConsultarPorId: Deve lançar exceção em caso de erro")
    void deveLancarExcecaoAoConsultarPorId() {
        RuntimeException exBanco = new RuntimeException("Erro Banco");
        when(repository.findById(ID)).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class, () -> provider.consultarPorId(ID));

        assertEquals(MSG_ERRO_CONSULTAR, ex.getMessage());
    }

    @Test
    @DisplayName("Deletar: Deve chamar consultarPorId e depois deleteById")
    void deveDeletarComSucesso() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        provider.deletar(ID);

        verify(repository).findById(ID);

        verify(repository).deleteById(ID);
    }

}