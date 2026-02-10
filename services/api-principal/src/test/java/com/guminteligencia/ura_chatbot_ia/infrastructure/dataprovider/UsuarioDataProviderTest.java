package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.UsuarioMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.UsuarioRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
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
class UsuarioDataProviderTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioDataProvider provider;

    private final UUID ID = UUID.randomUUID();
    private final String EMAIL = "teste@email.com";
    private final String TELEFONE = "5511999999999";

    // Mensagens de erro extraídas da classe original
    private static final String MSG_ERRO_ID = "Erro ao buscar usuário pelo id.";
    private static final String MSG_ERRO_SALVAR = "Erro ao salvar usuário.";
    private static final String MSG_ERRO_EMAIL = "Erro ao consultar usuário pelo email.";
    private static final String MSG_ERRO_DELETAR = "Erro ao deletar usuário.";
    private static final String MSG_ERRO_TELEFONE = "Erro ao consultar usuário pelo telefone conectado.";
    private static final String MSG_ERRO_LISTAR = "Erro ao listar todos os usuários.";

    @Test
    @DisplayName("ConsultarPorId: Deve retornar usuário mapeado quando encontrado")
    void deveConsultarPorIdComSucesso() {
        UsuarioEntity entity = new UsuarioEntity();
        Usuario domain = new Usuario();

        when(repository.findById(ID)).thenReturn(Optional.of(entity));

        try (MockedStatic<UsuarioMapper> ms = Mockito.mockStatic(UsuarioMapper.class)) {
            ms.when(() -> UsuarioMapper.paraDomain(entity)).thenReturn(domain);

            Optional<Usuario> result = provider.consultarPorId(ID);

            assertTrue(result.isPresent());
            assertEquals(domain, result.get());
            verify(repository).findById(ID);
        }
    }

    @Test
    @DisplayName("ConsultarPorId: Deve retornar vazio quando não encontrar")
    void deveRetornarVazioAoConsultarPorId() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        Optional<Usuario> result = provider.consultarPorId(ID);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("ConsultarPorId: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoConsultarPorId() {
        RuntimeException exBanco = new RuntimeException("Erro BD");
        when(repository.findById(ID)).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.consultarPorId(ID));

        assertEquals(MSG_ERRO_ID, ex.getMessage());
    }

    @Test
    @DisplayName("Salvar: Deve converter, salvar e retornar domínio")
    void deveSalvarComSucesso() {
        Usuario input = new Usuario();
        UsuarioEntity entityInput = new UsuarioEntity();
        UsuarioEntity entitySalva = new UsuarioEntity();
        Usuario output = new Usuario();

        when(repository.save(any(UsuarioEntity.class))).thenReturn(entitySalva);

        try (MockedStatic<UsuarioMapper> ms = Mockito.mockStatic(UsuarioMapper.class)) {
            ms.when(() -> UsuarioMapper.paraEntity(input)).thenReturn(entityInput);
            ms.when(() -> UsuarioMapper.paraDomain(entitySalva)).thenReturn(output);

            Usuario result = provider.salvar(input);

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

        try (MockedStatic<UsuarioMapper> ms = Mockito.mockStatic(UsuarioMapper.class)) {
            ms.when(() -> UsuarioMapper.paraEntity(any())).thenReturn(new UsuarioEntity());

            DataProviderException ex = assertThrows(DataProviderException.class,
                    () -> provider.salvar(new Usuario()));

            assertEquals(MSG_ERRO_SALVAR, ex.getMessage());
        }
    }

    @Test
    @DisplayName("ConsultarPorEmail: Deve retornar usuário quando encontrado")
    void deveConsultarPorEmailComSucesso() {
        UsuarioEntity entity = new UsuarioEntity();
        Usuario domain = new Usuario();

        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(entity));

        try (MockedStatic<UsuarioMapper> ms = Mockito.mockStatic(UsuarioMapper.class)) {
            ms.when(() -> UsuarioMapper.paraDomain(entity)).thenReturn(domain);

            Optional<Usuario> result = provider.consultarPorEmail(EMAIL);

            assertTrue(result.isPresent());
            assertEquals(domain, result.get());
        }
    }

    @Test
    @DisplayName("ConsultarPorEmail: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoConsultarPorEmail() {
        RuntimeException exBanco = new RuntimeException("Erro Email");
        when(repository.findByEmail(any())).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.consultarPorEmail(EMAIL));

        assertEquals(MSG_ERRO_EMAIL, ex.getMessage());
    }

    @Test
    @DisplayName("ConsultarPorTelefoneConectado: Deve retornar usuário quando encontrado")
    void deveConsultarPorTelefoneComSucesso() {
        UsuarioEntity entity = new UsuarioEntity();
        Usuario domain = new Usuario();

        when(repository.findByTelefoneConectado(TELEFONE)).thenReturn(Optional.of(entity));

        try (MockedStatic<UsuarioMapper> ms = Mockito.mockStatic(UsuarioMapper.class)) {
            ms.when(() -> UsuarioMapper.paraDomain(entity)).thenReturn(domain);

            Optional<Usuario> result = provider.consultarPorTelefoneConectado(TELEFONE);

            assertTrue(result.isPresent());
            assertEquals(domain, result.get());
        }
    }

    @Test
    @DisplayName("ConsultarPorTelefoneConectado: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoConsultarPorTelefone() {
        RuntimeException exBanco = new RuntimeException("Erro Telefone");
        when(repository.findByTelefoneConectado(any())).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.consultarPorTelefoneConectado(TELEFONE));

        assertEquals(MSG_ERRO_TELEFONE, ex.getMessage());
    }

    @Test
    @DisplayName("Listar: Deve retornar lista de usuários mapeada")
    void deveListarComSucesso() {
        UsuarioEntity entity = new UsuarioEntity();
        Usuario domain = new Usuario();
        List<UsuarioEntity> entities = List.of(entity);

        when(repository.findAll()).thenReturn(entities);

        try (MockedStatic<UsuarioMapper> ms = Mockito.mockStatic(UsuarioMapper.class)) {
            ms.when(() -> UsuarioMapper.paraDomain(entity)).thenReturn(domain);

            List<Usuario> result = provider.listar();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(domain, result.get(0));
        }
    }

    @Test
    @DisplayName("Listar: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoListar() {
        RuntimeException exBanco = new RuntimeException("Erro Listar");
        when(repository.findAll()).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.listar());

        assertEquals(MSG_ERRO_LISTAR, ex.getMessage());
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