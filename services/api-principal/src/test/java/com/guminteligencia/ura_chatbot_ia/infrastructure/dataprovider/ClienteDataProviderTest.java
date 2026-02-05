package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ObjetoRelatorioDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ClienteMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.RelatorioMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ClienteRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.objetoRelatorio.RelatorioProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ClienteDataProviderTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteDataProvider provider;

    private final String ERR_TEL = "Erro ao consultar cliente pelo seu telefone.";
    private final String ERR_SALVAR = "Erro ao salvar cliente.";
    private final String ERR_ID = "Erro ao consultar cliente pelo seu id.";
    private final String ERR_REL = "Erro ao gerar relatório de contatos.";
    private final String ERR_REL2 = "Erro ao gerar relatório de segunda feira.";

    private ClienteEntity entityIn;
    private ClienteEntity entityOut;
    private Cliente domainIn;
    private Cliente domainOut;
    private UUID id;
    private String telefone;

    private List<ObjetoRelatorioDto> dtoList;

    @BeforeEach
    void setup() {
        entityIn = mock(ClienteEntity.class);
        entityOut = mock(ClienteEntity.class);
        domainIn = mock(Cliente.class);
        domainOut = mock(Cliente.class);
        id = UUID.randomUUID();
        telefone = "+5511999999999";
        dtoList = new ArrayList<>(List.of(
                ObjetoRelatorioDto.builder()
                        .nome("Nome teste")
                        .telefone("telefone teste")
                        .atributos_qualificacao(Map.of("teste", "teste1", "teste2", "teste2"))
                        .data_criacao(LocalDateTime.now())
                        .nome_vendedor("VendedorTeste")
                        .build(),
                ObjetoRelatorioDto.builder()
                        .nome("Nome teste 2")
                        .telefone("telefone teste 2")
                        .atributos_qualificacao(Map.of("teste", "teste1", "teste2", "teste2"))
                        .data_criacao(LocalDateTime.now())
                        .nome_vendedor("VendedorTeste2")
                        .build()
        ));
    }

    @Test
    void deveConsultarPorTelefoneComSucesso() {
        when(repository.findByTelefone(telefone))
                .thenReturn(Optional.of(entityIn));

        try (MockedStatic<ClienteMapper> ms = mockStatic(ClienteMapper.class)) {
            ms.when(() -> ClienteMapper.paraDomain(entityIn))
                    .thenReturn(domainOut);

            Optional<Cliente> result = provider.consultarPorTelefone(telefone);
            assertTrue(result.isPresent());
            assertSame(domainOut, result.get());

            verify(repository).findByTelefone(telefone);
            ms.verify(() -> ClienteMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveRetornaVazioAoConsultarPorTelefone() {
        when(repository.findByTelefone(telefone))
                .thenReturn(Optional.empty());

        Optional<Cliente> result = provider.consultarPorTelefone(telefone);
        assertTrue(result.isEmpty());
        verify(repository).findByTelefone(telefone);
    }

    @Test
    void deveLancarExceptionAoConsultarPorTelefone() {
        when(repository.findByTelefone(anyString()))
                .thenThrow(new RuntimeException("fail-tel"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorTelefone(telefone)
        );
        assertEquals(ERR_TEL, ex.getMessage());
    }

    @Test
    void deveSalvarComSucesso() {
        try (MockedStatic<ClienteMapper> ms = mockStatic(ClienteMapper.class)) {
            ms.when(() -> ClienteMapper.paraEntity(domainIn))
                    .thenReturn(entityIn);
            when(repository.save(entityIn))
                    .thenReturn(entityOut);
            ms.when(() -> ClienteMapper.paraDomain(entityOut))
                    .thenReturn(domainOut);

            Cliente result = provider.salvar(domainIn);
            assertSame(domainOut, result);

            verify(repository).save(entityIn);
            ms.verify(() -> ClienteMapper.paraEntity(domainIn));
            ms.verify(() -> ClienteMapper.paraDomain(entityOut));
        }
    }

    @Test
    void deveLancarExceptionAoSalvar() {
        try (MockedStatic<ClienteMapper> ms = mockStatic(ClienteMapper.class)) {
            ms.when(() -> ClienteMapper.paraEntity(domainIn))
                    .thenReturn(entityIn);
            when(repository.save(entityIn))
                    .thenThrow(new RuntimeException("fail-save"));

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.salvar(domainIn)
            );
            assertEquals(ERR_SALVAR, ex.getMessage());
        }
    }

    @Test
    void deveConsultarPorIdComSucesso() {
        when(repository.findById(id))
                .thenReturn(Optional.of(entityIn));

        try (MockedStatic<ClienteMapper> ms = mockStatic(ClienteMapper.class)) {
            ms.when(() -> ClienteMapper.paraDomain(entityIn))
                    .thenReturn(domainOut);

            Optional<Cliente> result = provider.consultarPorId(id);
            assertTrue(result.isPresent());
            assertSame(domainOut, result.get());

            verify(repository).findById(id);
            ms.verify(() -> ClienteMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveRetornarVazioComSucessoAoConsultarPorId() {
        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Cliente> result = provider.consultarPorId(id);
        assertTrue(result.isEmpty());
        verify(repository).findById(id);
    }

    @Test
    void deveLancarExceptionAoConsultarPorId() {
        when(repository.findById(any(UUID.class)))
                .thenThrow(new RuntimeException("fail-id"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorId(id)
        );
        assertEquals(ERR_ID, ex.getMessage());
    }

    @Test
    void deveRetornarRelatoriosDeContatosComSucesso() {
        RelatorioProjection projectionMock = mock(RelatorioProjection.class);

        List<RelatorioProjection> raw = List.of(projectionMock);

        when(repository.gerarRelatorio(id)).thenReturn(raw);

        try (MockedStatic<RelatorioMapper> ms = mockStatic(RelatorioMapper.class)) {

            ms.when(() -> RelatorioMapper.paraDto(raw)).thenReturn(dtoList);

            List<ObjetoRelatorioDto> result = provider.getRelatorioContato(id);

            assertNotNull(result);
            assertSame(dtoList, result);

            verify(repository).gerarRelatorio(id);
            ms.verify(() -> RelatorioMapper.paraDto(raw));
        }
    }

    @Test
    void deveLancarExceptionAoRetornarRelatorioDeContatos() {
        when(repository.gerarRelatorio(Mockito.any()))
                .thenThrow(new RuntimeException("fail-rel"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.getRelatorioContato(Mockito.any())
        );
        assertEquals(ERR_REL, ex.getMessage());
    }


    @Test
    void deveRetornaRelatorioDeContatosDeSegundaFeira() {
        RelatorioProjection projectionMock = mock(RelatorioProjection.class);

        List<RelatorioProjection> raw = List.of(projectionMock);

        when(repository.gerarRelatorioSegundaFeira(id)).thenReturn(raw);

        try (MockedStatic<RelatorioMapper> ms = mockStatic(RelatorioMapper.class)) {

            ms.when(() -> RelatorioMapper.paraDto(raw)).thenReturn(dtoList);

            List<ObjetoRelatorioDto> result = provider.getRelatorioContatoSegundaFeira(id);

            assertNotNull(result);
            assertSame(dtoList, result);

            verify(repository).gerarRelatorioSegundaFeira(id);
            ms.verify(() -> RelatorioMapper.paraDto(raw));
        }
    }

    @Test
    void deveLancarExceptionAoRetornarRelatorioDeContatosDeSegundaFeira() {
        when(repository.gerarRelatorioSegundaFeira(Mockito.any()))
                .thenThrow(new RuntimeException("fail-rel2"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.getRelatorioContatoSegundaFeira(Mockito.any())
        );
        assertEquals(ERR_REL2, ex.getMessage());
    }

    @Test
    @DisplayName("Deve retornar cliente quando encontrado no repositório")
    void deveRetornarClienteQuandoEncontrado() {
        // Arrange
        ClienteEntity entity = new ClienteEntity();
        entity.setId(id);
        entity.setNome("Cliente Teste");
        entity.setTelefone(telefone);
        // Não precisamos setar o UsuarioEntity, pois vamos mockar o Mapper

        // Objeto de domínio que o Mapper vai "fingir" que retornou
        Cliente clienteEsperado = new Cliente();
        clienteEsperado.setNome("Cliente Teste");

        when(repository.findByTelefoneAndUsuario_Id(telefone, id))
                .thenReturn(Optional.of(entity));

        // --- CORREÇÃO: Usar try-with-resources para Mockar o Static ---
        try (MockedStatic<ClienteMapper> ms = mockStatic(ClienteMapper.class)) {

            // Ensinamos o Mock: "Quando chamarem paraDomain com essa entity, retorne esse clienteEsperado"
            ms.when(() -> ClienteMapper.paraDomain(entity))
                    .thenReturn(clienteEsperado);

            // Act
            Optional<Cliente> resultado = provider.consultarPorTelefoneEUsuario(telefone, id);

            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(clienteEsperado.getNome(), resultado.get().getNome());

            verify(repository).findByTelefoneAndUsuario_Id(telefone, id);

            // Verifica se o mapper foi chamado
            ms.verify(() -> ClienteMapper.paraDomain(entity));
        }
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando não encontrar no repositório")
    void deveRetornarVazioQuandoNaoEncontrado() {
        // Arrange
        when(repository.findByTelefoneAndUsuario_Id(telefone, id))
                .thenReturn(Optional.empty());

        // Act
        Optional<Cliente> resultado = provider.consultarPorTelefoneEUsuario(telefone, id);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(repository).findByTelefoneAndUsuario_Id(telefone, id);
    }

    @Test
    @DisplayName("Deve lançar DataProviderException quando ocorrer erro no repositório")
    void deveLancarExcecaoQuandoRepositorioFalhar() {
        RuntimeException exBanco = new RuntimeException("Erro de conexão");

        when(repository.findByTelefoneAndUsuario_Id(anyString(), any(UUID.class)))
                .thenThrow(exBanco);

        DataProviderException exception = assertThrows(DataProviderException.class,
                () -> provider.consultarPorTelefoneEUsuario(telefone, id));

        assertNotNull(exception.getMessage());
    }


    @Test
    @DisplayName("Cenário com Mock Static do Mapper (Opcional - Mais Seguro)")
    void deveMapearCorretamenteUsandoMockStatic() {
        // Arrange
        ClienteEntity entity = new ClienteEntity();
        Cliente dominioEsperado = new Cliente();
        dominioEsperado.setNome("Mockado");

        when(repository.findByTelefoneAndUsuario_Id(telefone, id))
                .thenReturn(Optional.of(entity));

        // Mock do método estático (Requer dependência mockito-inline)
        try (MockedStatic<ClienteMapper> mapperMock = Mockito.mockStatic(ClienteMapper.class)) {
            mapperMock.when(() -> ClienteMapper.paraDomain(entity))
                    .thenReturn(dominioEsperado);

            // Act
            Optional<Cliente> resultado = provider.consultarPorTelefoneEUsuario(telefone, id);

            // Assert
            assertTrue(resultado.isPresent());
            assertEquals("Mockado", resultado.get().getNome());
        }
    }
}