package com.gumeinteligencia.api_intermidiaria.infrastructure.dataprovider;

import com.gumeinteligencia.api_intermidiaria.domain.Cliente;
import com.gumeinteligencia.api_intermidiaria.infrastructure.exceptions.DataProviderException;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.CLienteRepository;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ClienteEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteDataProviderTest {

    @Mock
    private CLienteRepository repository;

    @InjectMocks
    private ClienteDataProvider dataProvider;

    @Test
    void deveConsultarClienteComSucesso() {
        ClienteEntity entity = ClienteEntity.builder()
                .id(UUID.randomUUID())
                .nome("Fulano")
                .telefone("44999999999")
                .inativo(false)
                .build();

        when(repository.findByTelefoneAndInativoFalse(entity.getTelefone())).thenReturn(Optional.of(entity));

        Optional<Cliente> resultado = dataProvider.consultarPorTelefone(entity.getTelefone());

        assertTrue(resultado.isPresent());
        assertEquals(entity.getTelefone(), resultado.get().getTelefone());
        verify(repository).findByTelefoneAndInativoFalse(entity.getTelefone());
    }

    @Test
    void deveLancarDataProviderExceptionQuandoRepositorioFalhar() {
        when(repository.findByTelefoneAndInativoFalse(anyString()))
                .thenThrow(new RuntimeException("falha"));

        DataProviderException ex = assertThrows(DataProviderException.class, () ->
                dataProvider.consultarPorTelefone("123"));

        assertEquals("Erro ao consultar cliente pelo telefone.", ex.getMessage());
    }
}
