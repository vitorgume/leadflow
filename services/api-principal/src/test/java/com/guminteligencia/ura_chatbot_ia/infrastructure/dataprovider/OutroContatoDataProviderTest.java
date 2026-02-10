package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.TipoContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.OutroContatoMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.OutroContatoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OutroContatoDataProviderTest {

    @Mock
    private OutroContatoRepository repository;

    @InjectMocks
    private OutroContatoDataProvider provider;

    private final UUID ID_USUARIO = UUID.randomUUID();
    private final Long ID_CONTATO = 1L;
    private final String NOME = "Contato Teste";
    private final String TELEFONE = "5511999999999";

    // Mensagens de erro copiadas da classe original para validação
    private static final String MSG_ERRO_NOME = "Erro ao consultar por nome outro contato.";
    private static final String MSG_ERRO_TIPO = "Erro ao consultra por tipo outro contato.";
    private static final String MSG_ERRO_TELEFONE = "Erro ao consultar por telefone outro contato.";
    private static final String MSG_ERRO_SALVAR = "Erro ao salvar outro contato.";
    private static final String MSG_ERRO_ID = "Erro ao consultar por id outro contato.";
    private static final String MSG_ERRO_LISTAR = "Erro ao listar por usuario outro contato.";
    private static final String MSG_ERRO_DELETAR = "Erro ao deletar por id outro contato.";

    @Test
    @DisplayName("ConsultarPorNome: Deve retornar contato mapeado")
    void deveConsultarPorNomeComSucesso() {
        OutroContatoEntity entity = new OutroContatoEntity();
        OutroContato domain = new OutroContato();

        when(repository.findByNome(NOME)).thenReturn(Optional.of(entity));

        try (MockedStatic<OutroContatoMapper> ms = Mockito.mockStatic(OutroContatoMapper.class)) {
            ms.when(() -> OutroContatoMapper.paraDomain(any(OutroContatoEntity.class)))
                    .thenReturn(domain);

            Optional<OutroContato> result = provider.consultarPorNome(NOME);

            assertTrue(result.isPresent());
            assertEquals(domain, result.get());
        }
    }

    @Test
    @DisplayName("ConsultarPorNome: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoConsultarPorNome() {
        RuntimeException exBanco = new RuntimeException("Erro BD");
        when(repository.findByNome(any())).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.consultarPorNome(NOME));

        assertEquals(MSG_ERRO_NOME, ex.getMessage());
    }

    @Test
    @DisplayName("ConsultarPorTipo: Deve retornar contato mapeado")
    void deveConsultarPorTipoComSucesso() {
        TipoContato tipo = TipoContato.PADRAO; // Exemplo
        OutroContatoEntity entity = new OutroContatoEntity();
        OutroContato domain = new OutroContato();

        when(repository.findByTipoContatoAndUsuario_Id(tipo, ID_USUARIO))
                .thenReturn(Optional.of(entity));

        try (MockedStatic<OutroContatoMapper> ms = Mockito.mockStatic(OutroContatoMapper.class)) {
            ms.when(() -> OutroContatoMapper.paraDomain(entity)).thenReturn(domain);

            Optional<OutroContato> result = provider.consultarPorTipo(tipo, ID_USUARIO);

            assertTrue(result.isPresent());
            assertEquals(domain, result.get());
        }
    }

    @Test
    @DisplayName("ConsultarPorTipo: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoConsultarPorTipo() {
        RuntimeException exBanco = new RuntimeException("Erro BD");
        when(repository.findByTipoContatoAndUsuario_Id(any(), any())).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.consultarPorTipo(TipoContato.PADRAO, ID_USUARIO));

        assertEquals(MSG_ERRO_TIPO, ex.getMessage());
    }

    @Test
    @DisplayName("ConsultarPorTelefone: Deve retornar contato mapeado")
    void deveConsultarPorTelefoneComSucesso() {
        OutroContatoEntity entity = new OutroContatoEntity();
        OutroContato domain = new OutroContato();

        when(repository.findByTelefone(TELEFONE)).thenReturn(Optional.of(entity));

        try (MockedStatic<OutroContatoMapper> ms = Mockito.mockStatic(OutroContatoMapper.class)) {
            ms.when(() -> OutroContatoMapper.paraDomain(entity)).thenReturn(domain);

            Optional<OutroContato> result = provider.consultarPorTelefone(TELEFONE);

            assertTrue(result.isPresent());
            assertEquals(domain, result.get());
        }
    }

    @Test
    @DisplayName("ConsultarPorTelefone: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoConsultarPorTelefone() {
        RuntimeException exBanco = new RuntimeException("Erro BD");
        when(repository.findByTelefone(any())).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.consultarPorTelefone(TELEFONE));

        assertEquals(MSG_ERRO_TELEFONE, ex.getMessage());
    }

    @Test
    @DisplayName("Salvar: Deve converter, salvar e retornar domínio")
    void deveSalvarComSucesso() {
        OutroContato input = new OutroContato();
        OutroContatoEntity entityInput = new OutroContatoEntity();
        OutroContatoEntity entitySalva = new OutroContatoEntity();
        OutroContato output = new OutroContato();

        when(repository.save(any(OutroContatoEntity.class))).thenReturn(entitySalva);

        try (MockedStatic<OutroContatoMapper> ms = Mockito.mockStatic(OutroContatoMapper.class)) {
            ms.when(() -> OutroContatoMapper.paraEntity(input)).thenReturn(entityInput);
            ms.when(() -> OutroContatoMapper.paraDomain(entitySalva)).thenReturn(output);

            OutroContato result = provider.salvar(input);

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

        try (MockedStatic<OutroContatoMapper> ms = Mockito.mockStatic(OutroContatoMapper.class)) {
            ms.when(() -> OutroContatoMapper.paraEntity(any())).thenReturn(new OutroContatoEntity());

            DataProviderException ex = assertThrows(DataProviderException.class,
                    () -> provider.salvar(new OutroContato()));

            assertEquals(MSG_ERRO_SALVAR, ex.getMessage());
        }
    }

    @Test
    @DisplayName("ConsultarPorId: Deve retornar contato mapeado")
    void deveConsultarPorIdComSucesso() {
        OutroContatoEntity entity = new OutroContatoEntity();
        OutroContato domain = new OutroContato();

        when(repository.findById(ID_CONTATO)).thenReturn(Optional.of(entity));

        try (MockedStatic<OutroContatoMapper> ms = Mockito.mockStatic(OutroContatoMapper.class)) {
            ms.when(() -> OutroContatoMapper.paraDomain(entity)).thenReturn(domain);

            Optional<OutroContato> result = provider.consultarPorId(ID_CONTATO);

            assertTrue(result.isPresent());
            assertEquals(domain, result.get());
        }
    }

    @Test
    @DisplayName("ConsultarPorId: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoConsultarPorId() {
        RuntimeException exBanco = new RuntimeException("Erro ID");
        when(repository.findById(anyLong())).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.consultarPorId(ID_CONTATO));

        assertEquals(MSG_ERRO_ID, ex.getMessage());
    }

    @Test
    @DisplayName("Listar: Deve retornar página mapeada")
    void deveListarComSucesso() {
        Pageable pageable = Pageable.unpaged();
        OutroContatoEntity entity = new OutroContatoEntity();
        Page<OutroContatoEntity> pageEntity = new PageImpl<>(List.of(entity));
        OutroContato domain = new OutroContato();

        when(repository.findByUsuario_Id(pageable, ID_USUARIO)).thenReturn(pageEntity);

        try (MockedStatic<OutroContatoMapper> ms = Mockito.mockStatic(OutroContatoMapper.class)) {
            ms.when(() -> OutroContatoMapper.paraDomain(entity)).thenReturn(domain);

            Page<OutroContato> result = provider.listar(pageable, ID_USUARIO);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(domain, result.getContent().get(0));
        }
    }

    @Test
    @DisplayName("Listar: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoListar() {
        RuntimeException exBanco = new RuntimeException("Erro Lista");
        when(repository.findByUsuario_Id(any(), any())).thenThrow(exBanco);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.listar(Pageable.unpaged(), ID_USUARIO));

        assertEquals(MSG_ERRO_LISTAR, ex.getMessage());
    }

    @Test
    @DisplayName("Deletar: Deve chamar deleteById com sucesso")
    void deveDeletarComSucesso() {
        provider.deletar(ID_CONTATO);
        verify(repository).deleteById(ID_CONTATO);
    }

    @Test
    @DisplayName("Deletar: Deve lançar exceção ao falhar")
    void deveLancarExcecaoAoDeletar() {
        RuntimeException exBanco = new RuntimeException("Erro Delete");
        doThrow(exBanco).when(repository).deleteById(ID_CONTATO);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.deletar(ID_CONTATO));

        assertEquals(MSG_ERRO_DELETAR, ex.getMessage());
    }

}