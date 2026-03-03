package com.gumeinteligencia.api_intermidiaria.infrastructure.dataprovider;

import com.gumeinteligencia.api_intermidiaria.domain.ConversaAgente;
import com.gumeinteligencia.api_intermidiaria.infrastructure.exceptions.DataProviderException;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.ConversaAgenteRepository;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ClienteEntity;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ConversaAgenteEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversaAgenteDataProviderTest {

    @Mock
    private ConversaAgenteRepository repository;

    @InjectMocks
    private ConversaAgenteDataProvider dataProvider;

    private final String TELEFONE_CLIENTE = "45999999999";

    @Test
    void deveConsultarPorTelefoneClienteRetornandoConversaComSucesso() {
        // Prepara
        ConversaAgenteEntity entity = ConversaAgenteEntity.builder().id(UUID.randomUUID()).cliente(ClienteEntity.builder().id(UUID.randomUUID()).build()).build();
        when(repository.findByCliente_Telefone(TELEFONE_CLIENTE)).thenReturn(Optional.of(entity));

        // Executa
        Optional<ConversaAgente> resultado = dataProvider.consultarPorTelefoneCliente(TELEFONE_CLIENTE);

        // Verifica
        assertTrue(resultado.isPresent(), "Deveria retornar uma conversa mapeada");
        verify(repository, times(1)).findByCliente_Telefone(TELEFONE_CLIENTE);
    }

    @Test
    void deveConsultarPorTelefoneClienteRetornandoVazioQuandoNaoEncontrar() {
        // Prepara
        when(repository.findByCliente_Telefone(TELEFONE_CLIENTE)).thenReturn(Optional.empty());

        // Executa
        Optional<ConversaAgente> resultado = dataProvider.consultarPorTelefoneCliente(TELEFONE_CLIENTE);

        // Verifica
        assertTrue(resultado.isEmpty(), "Deveria retornar Optional.empty()");
        verify(repository, times(1)).findByCliente_Telefone(TELEFONE_CLIENTE);
    }

    @Test
    void deveLancarDataProviderExceptionQuandoRepositoryFalhar() {
        // Prepara
        RuntimeException erroBanco = new RuntimeException("Falha na conexão com o banco de dados");
        when(repository.findByCliente_Telefone(TELEFONE_CLIENTE)).thenThrow(erroBanco);

        // Executa e Verifica se a exceção correta foi lançada
        DataProviderException exception = Assertions.assertThrows(DataProviderException.class, () -> {
            dataProvider.consultarPorTelefoneCliente(TELEFONE_CLIENTE);
        });

        // Verifica se a mensagem customizada está correta
        assertEquals("Erro ao consultar conversa pelo telefone do cliente.", exception.getMessage());
        verify(repository, times(1)).findByCliente_Telefone(TELEFONE_CLIENTE);
    }
}