package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import static org.junit.jupiter.api.Assertions.*;

import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.MembroMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.MembroRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MembroEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
class MembroDataProviderTest {

    @Mock
    private MembroRepository repository;

    @InjectMocks
    private MembroDataProvider provider;

    private final UUID ID = UUID.randomUUID();
    private final UUID ID_USUARIO = UUID.randomUUID();
    private final String TELEFONE = "5511999999999";

    // Mensagens de erro extraídas da classe original
    private static final String MSG_ERRO_CONSULTAR_POR_TELEFONE = "Erro ao consultar membro por telefone.";
    private static final String MSG_ERRO_SALVAR = "Erro ao salvar membro.";
    private static final String MSG_ERRO_LISTAR = "Erro ao listar membros.";
    private static final String MSG_ERRO_CONSULTAR_POR_ID = "Erro ao consultar membro por id.";
    private static final String MSG_ERRO_DELETAR = "Erro ao deletar membro.";

    @Test
    @DisplayName("ConsultarPorTelefone: Deve retornar membro mapeado quando encontrado")
    void deveConsultarPorTelefoneComSucesso() {
        MembroEntity entity = new MembroEntity();
        Membro domain = new Membro();

        when(repository.findByTelefone(TELEFONE)).thenReturn(Optional.of(entity));

        try (MockedStatic<MembroMapper> ms = Mockito.mockStatic(MembroMapper.class)) {
            ms.when(() -> MembroMapper.paraDomain(entity)).thenReturn(domain);

            Optional<Membro> result = provider.consultarPorTelefone(TELEFONE);

            assertTrue(result.isPresent());
            assertEquals(domain, result.get());
            verify(repository).findByTelefone(TELEFONE);
        }
    }

    @Test
    @DisplayName("ConsultarPorTelefone: Deve retornar vazio quando não encontrar")
    void deveRetornarVazioAoConsultarPorTelefone() {
        when(repository.findByTelefone(TELEFONE)).thenReturn(Optional.empty());

        Optional<Membro> result = provider.consultarPorTelefone(TELEFONE);

        assertTrue(result.isEmpty());
        verify(repository).findByTelefone(TELEFONE);
    }

    @Test
    @DisplayName("ConsultarPorTelefone: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoConsultarPorTelefone() {
        RuntimeException exBanco = new RuntimeException("Erro BD");
        when(repository.findByTelefone(any())).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.consultarPorTelefone(TELEFONE));

        assertEquals(MSG_ERRO_CONSULTAR_POR_TELEFONE, ex.getMessage());
    }

    @Test
    @DisplayName("Salvar: Deve converter, salvar e retornar domínio")
    void deveSalvarComSucesso() {
        Membro input = new Membro();
        MembroEntity entityInput = new MembroEntity();
        MembroEntity entitySalva = new MembroEntity();
        Membro output = new Membro();

        when(repository.save(any(MembroEntity.class))).thenReturn(entitySalva);

        try (MockedStatic<MembroMapper> ms = Mockito.mockStatic(MembroMapper.class)) {
            ms.when(() -> MembroMapper.paraEntity(input)).thenReturn(entityInput);
            ms.when(() -> MembroMapper.paraDomain(entitySalva)).thenReturn(output);

            Membro result = provider.salvar(input);

            assertNotNull(result);
            assertEquals(output, result);
            verify(repository).save(entityInput);
        }
    }

    @Test
    @DisplayName("Salvar: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoSalvar() {
        RuntimeException exBanco = new RuntimeException("Erro Salvar");
        when(repository.save(any())).thenThrow(exBanco);

        try (MockedStatic<MembroMapper> ms = Mockito.mockStatic(MembroMapper.class)) {
            ms.when(() -> MembroMapper.paraEntity(any())).thenReturn(new MembroEntity());

            DataProviderException ex = assertThrows(DataProviderException.class,
                    () -> provider.salvar(new Membro()));

            assertEquals(MSG_ERRO_SALVAR, ex.getMessage());
        }
    }

    @Test
    @DisplayName("Listar: Deve retornar lista de membros mapeada por id do usuario")
    void deveListarComSucesso() {
        MembroEntity entity = new MembroEntity();
        Membro domain = new Membro();
        List<MembroEntity> entities = List.of(entity);

        when(repository.findByUsuario_Id(ID_USUARIO)).thenReturn(entities);

        try (MockedStatic<MembroMapper> ms = Mockito.mockStatic(MembroMapper.class)) {
            ms.when(() -> MembroMapper.paraDomain(entity)).thenReturn(domain);

            List<Membro> result = provider.listar(ID_USUARIO);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(domain, result.get(0));
            verify(repository).findByUsuario_Id(ID_USUARIO);
        }
    }

    @Test
    @DisplayName("Listar: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoListar() {
        RuntimeException exBanco = new RuntimeException("Erro Listar");
        when(repository.findByUsuario_Id(any())).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.listar(ID_USUARIO));

        assertEquals(MSG_ERRO_LISTAR, ex.getMessage());
    }

    @Test
    @DisplayName("ConsultarPorId: Deve retornar membro mapeado quando encontrado")
    void deveConsultarPorIdComSucesso() {
        MembroEntity entity = new MembroEntity();
        Membro domain = new Membro();

        when(repository.findById(ID)).thenReturn(Optional.of(entity));

        try (MockedStatic<MembroMapper> ms = Mockito.mockStatic(MembroMapper.class)) {
            ms.when(() -> MembroMapper.paraDomain(entity)).thenReturn(domain);

            Optional<Membro> result = provider.consultarPorId(ID);

            assertTrue(result.isPresent());
            assertEquals(domain, result.get());
            verify(repository).findById(ID);
        }
    }

    @Test
    @DisplayName("ConsultarPorId: Deve retornar vazio quando não encontrar")
    void deveRetornarVazioAoConsultarPorId() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        Optional<Membro> result = provider.consultarPorId(ID);

        assertTrue(result.isEmpty());
        verify(repository).findById(ID);
    }

    @Test
    @DisplayName("ConsultarPorId: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoConsultarPorId() {
        RuntimeException exBanco = new RuntimeException("Erro BD");
        when(repository.findById(ID)).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.consultarPorId(ID));

        assertEquals(MSG_ERRO_CONSULTAR_POR_ID, ex.getMessage());
    }

    @Test
    @DisplayName("Deletar: Deve chamar deleteById com sucesso")
    void deveDeletarComSucesso() {
        provider.deletar(ID);
        verify(repository).deleteById(ID);
    }

    @Test
    @DisplayName("Deletar: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoDeletar() {
        RuntimeException exBanco = new RuntimeException("Erro Delete");
        doThrow(exBanco).when(repository).deleteById(ID);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.deletar(ID));

        assertEquals(MSG_ERRO_DELETAR, ex.getMessage());
    }
}