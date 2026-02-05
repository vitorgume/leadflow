package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.VendedorMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.VendedorRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.VendedorEntity;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class VendedorDataProviderTest {

    @Mock
    private VendedorRepository repository;

    @InjectMocks
    private VendedorDataProvider provider;

    private final String ERR_NAME = "Erro ao consultar vendedor pelo seu nome.";
    private final String ERR_LIST = "Erro ao listar vendedores.";
    private final String ERR_LIST_EX = "Erro ao listar todos os vendedores com excecao.";
    private final String ERR_SAVE = "Erro ao salvar novo vendedor.";
    private final String ERR_TEL = "Erro ao consultar vendedor pelo telefone.";
    private final String ERR_DELETE = "Erro ao deletar vendedor.";
    private final String ERR_ID = "Erro ao consultar vendedor pelo seu id.";
    private final String ERR_ATIVOS = "Erro ao listar vendedores ativos.";

    private VendedorEntity entityIn;
    private VendedorEntity entityOut;
    private Vendedor domainIn;
    private Vendedor domainOut;
    private final UUID ID_USUARIO = UUID.randomUUID();

    @BeforeEach
    void setup() {
        entityIn = mock(VendedorEntity.class);
        entityOut = mock(VendedorEntity.class);
        domainIn = mock(Vendedor.class);
        domainOut = mock(Vendedor.class);
    }

    @Test
    void deveConsultarVendedorPeloNomeComSucesso() {
        when(repository.findByNome("nome")).thenReturn(Optional.of(entityIn));
        try (MockedStatic<VendedorMapper> ms = mockStatic(VendedorMapper.class)) {
            ms.when(() -> VendedorMapper.paraDomain(entityIn)).thenReturn(domainOut);

            Optional<Vendedor> result = provider.consultarVendedor("nome");
            assertTrue(result.isPresent());
            assertSame(domainOut, result.get());

            verify(repository).findByNome("nome");
            ms.verify(() -> VendedorMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveRetornaVazioAoConsultarPorNome() {
        when(repository.findByNome("x")).thenReturn(Optional.empty());
        Optional<Vendedor> res = provider.consultarVendedor("x");
        assertTrue(res.isEmpty());
        verify(repository).findByNome("x");
    }

    @Test
    void deveLancarExceptionAoConsultarPorNome() {
        when(repository.findByNome(any()))
                .thenThrow(new RuntimeException("fail-name"));
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarVendedor("nome")
        );
        assertEquals(ERR_NAME, ex.getMessage());
    }

    @Test
    void deveListarPorUsuarioComSucesso() {
        List<VendedorEntity> raw = List.of(entityIn);
        List<Vendedor> domList = List.of(domainOut);

        when(repository.findByUsuario_Id(Mockito.any())).thenReturn(raw);
        try (MockedStatic<VendedorMapper> ms = mockStatic(VendedorMapper.class)) {
            ms.when(() -> VendedorMapper.paraDomain(entityIn)).thenReturn(domainOut);

            List<Vendedor> result = provider.listarPorUsuario(ID_USUARIO);
            assertEquals(domList, result);

            verify(repository).findByUsuario_Id(Mockito.any());
            ms.verify(() -> VendedorMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveLancarExceptionAoListarPorUsuario() {
        when(repository.findByUsuario_Id(Mockito.any())).thenThrow(new RuntimeException("fail-list"));
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.listarPorUsuario(Mockito.any())
        );
        assertEquals(ERR_LIST, ex.getMessage());
    }

    @Test
    void deveListarPorUsuarioComExcecaoComSucesso() {
        List<VendedorEntity> raw = List.of(entityIn);
        List<Vendedor> domList = List.of(domainOut);

        when(repository.listarComExcecao("exc")).thenReturn(raw);
        try (MockedStatic<VendedorMapper> ms = mockStatic(VendedorMapper.class)) {
            ms.when(() -> VendedorMapper.paraDomain(entityIn)).thenReturn(domainOut);

            List<Vendedor> result = provider.listarComExcecao("exc");
            assertEquals(domList, result);

            verify(repository).listarComExcecao("exc");
            ms.verify(() -> VendedorMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveLancarExceptionAoListarPorUsuarioComExcecao() {
        when(repository.listarComExcecao(any()))
                .thenThrow(new RuntimeException("fail-exc"));
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.listarComExcecao("exc")
        );
        assertEquals(ERR_LIST_EX, ex.getMessage());
    }

    @Test
    void deveSalvarComSucesso() {
        try (MockedStatic<VendedorMapper> ms = mockStatic(VendedorMapper.class)) {
            ms.when(() -> VendedorMapper.paraEntity(domainIn)).thenReturn(entityIn);
            when(repository.save(entityIn)).thenReturn(entityOut);
            ms.when(() -> VendedorMapper.paraDomain(entityOut)).thenReturn(domainOut);

            Vendedor result = provider.salvar(domainIn);
            assertSame(domainOut, result);

            verify(repository).save(entityIn);
            ms.verify(() -> VendedorMapper.paraEntity(domainIn));
            ms.verify(() -> VendedorMapper.paraDomain(entityOut));
        }
    }

    @Test
    void deveLancarExceptionAoSalvar() {
        try (MockedStatic<VendedorMapper> ms = mockStatic(VendedorMapper.class)) {
            ms.when(() -> VendedorMapper.paraEntity(domainIn)).thenReturn(entityIn);
            when(repository.save(entityIn))
                    .thenThrow(new RuntimeException("fail-save"));

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.salvar(domainIn)
            );
            assertEquals(ERR_SAVE, ex.getMessage());
        }
    }

    @Test
    void deveConsultarPorTelefoneComSucesso() {
        when(repository.findByTelefone("tel")).thenReturn(Optional.of(entityIn));
        try (MockedStatic<VendedorMapper> ms = mockStatic(VendedorMapper.class)) {
            ms.when(() -> VendedorMapper.paraDomain(entityIn)).thenReturn(domainOut);

            Optional<Vendedor> res = provider.consultarPorTelefone("tel");
            assertTrue(res.isPresent());
            assertSame(domainOut, res.get());

            verify(repository).findByTelefone("tel");
            ms.verify(() -> VendedorMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveRetornarVazioAoConsultarPorTelefone() {
        when(repository.findByTelefone("x")).thenReturn(Optional.empty());
        assertTrue(provider.consultarPorTelefone("x").isEmpty());
        verify(repository).findByTelefone("x");
    }

    @Test
    void deveLancarExceptionAoConsultarPorTelefone() {
        when(repository.findByTelefone(any()))
                .thenThrow(new RuntimeException("fail-tel"));
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorTelefone("tel")
        );
        assertEquals(ERR_TEL, ex.getMessage());
    }

    @Test
    void deveDeletarComSucesso() {
        provider.deletar(123L);
        verify(repository).deleteById(123L);
    }

    @Test
    void deveLancarExceptionAoDeletar() {
        doThrow(new RuntimeException("fail-del"))
                .when(repository).deleteById(456L);
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.deletar(456L)
        );
        assertEquals(ERR_DELETE, ex.getMessage());
    }

    @Test
    void deveConsultarPorIdComSucesso() {
        when(repository.findById(77L)).thenReturn(Optional.of(entityIn));
        try (MockedStatic<VendedorMapper> ms = mockStatic(VendedorMapper.class)) {
            ms.when(() -> VendedorMapper.paraDomain(entityIn)).thenReturn(domainOut);

            Optional<Vendedor> res = provider.consultarPorId(77L);
            assertTrue(res.isPresent());
            assertSame(domainOut, res.get());

            verify(repository).findById(77L);
            ms.verify(() -> VendedorMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveRetornarVazioAoConsultarPorId() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(provider.consultarPorId(99L).isEmpty());
        verify(repository).findById(99L);
    }

    @Test
    void deveLancarExceptionAoConsultarPorId() {
        when(repository.findById(anyLong()))
                .thenThrow(new RuntimeException("fail-id"));
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorId(5L)
        );
        assertEquals(ERR_ID, ex.getMessage());
    }

    @Test
    void deveListarPorUsuarioAtivosComSucesso() {
        List<VendedorEntity> raw = List.of(entityIn);
        List<Vendedor> domList = List.of(domainOut);

        when(repository.findByInativoIsFalse()).thenReturn(raw);
        try (MockedStatic<VendedorMapper> ms = mockStatic(VendedorMapper.class)) {
            ms.when(() -> VendedorMapper.paraDomain(entityIn)).thenReturn(domainOut);

            List<Vendedor> res = provider.listarAtivos();
            assertEquals(domList, res);

            verify(repository).findByInativoIsFalse();
            ms.verify(() -> VendedorMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveLancarExceptionAoListaraAtivos() {
        when(repository.findByInativoIsFalse())
                .thenThrow(new RuntimeException("fail-ativos"));
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.listarAtivos()
        );
        assertEquals(ERR_ATIVOS, ex.getMessage());
    }
}