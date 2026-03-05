package com.gumeinteligencia.api_intermidiaria.infrastructure.dataprovider;

import com.gumeinteligencia.api_intermidiaria.domain.Usuario;
import com.gumeinteligencia.api_intermidiaria.infrastructure.exceptions.DataProviderException;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.UsuarioRepository;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.UsuarioEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsuarioDataProviderTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioDataProvider dataProvider;

    private UsuarioEntity usuarioEntity;
    private final String TELEFONE_CONECTADO = "554432659874";

    @BeforeEach
    void setUp() {
        usuarioEntity = UsuarioEntity.builder()
                .id(UUID.randomUUID())
                .nome("Usuario teste")
                .telefone("554498748356")
                .email("userteste@gmail.com")
                .telefoneConectado(TELEFONE_CONECTADO)
                .softwareLigado(true)
                .build();
    }

    @Test
    void deveConsultarUsuarioPeloTelefoneConectadoComSucesso() {
        Mockito.when(repository.findByTelefoneConectado(TELEFONE_CONECTADO)).thenReturn(Optional.of(usuarioEntity));

        Optional<Usuario> result = dataProvider.consultarPorTelefoneConectado(TELEFONE_CONECTADO);

        Assertions.assertEquals(result.get().getId(), usuarioEntity.getId());

        Mockito.verify(repository).findByTelefoneConectado(TELEFONE_CONECTADO);
    }

    @Test
    void deveLancarExceptionQuandoTemAlgumErro() {
        Mockito.when(repository.findByTelefoneConectado(TELEFONE_CONECTADO)).thenThrow(RuntimeException.class);

        DataProviderException ex = Assertions.assertThrows(DataProviderException.class,
                () -> dataProvider.consultarPorTelefoneConectado(TELEFONE_CONECTADO));

        Assertions.assertEquals(ex.getMessage(), "Erro ao consultar usuário pelo telefone conectado.");
    }
}