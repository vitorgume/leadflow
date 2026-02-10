package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.CondicaoNaoEncontradaException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.CondicaoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConectorLogico;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CondicaoUseCaseTest {

    @Mock
    private CondicaoGateway gateway;

    @InjectMocks
    private CondicaoUseCase useCase;

    private Condicao condicao;

    @BeforeEach
    void setUp() {
        condicao = Condicao.builder()
                .id(UUID.randomUUID())
                .campo("teste")
                .operadorLogico(OperadorLogico.CONTAINS)
                .valor("testevalor")
                .conectorLogico(ConectorLogico.AND)
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar condição com sucesso")
    void deveCadastrarComSucesso() {
        // Arrange
        when(gateway.salvar(condicao)).thenReturn(condicao);

        // Act
        Condicao result = useCase.cadastrar(condicao);

        // Assert
        assertNotNull(result);
        verify(gateway).salvar(condicao);
    }

    @Test
    @DisplayName("Deve consultar por ID e retornar condição existente")
    void deveConsultarPorIdComSucesso() {

        when(gateway.consultarPorId(condicao.getId())).thenReturn(Optional.of(condicao));

        // Act
        Condicao result = useCase.consultarPorId(condicao.getId());

        // Assert
        assertNotNull(result);
        assertEquals(condicao, result);
    }

    @Test
    @DisplayName("Deve lançar exceção ao consultar ID inexistente")
    void deveLancarErroAoConsultarIdInexistente() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(gateway.consultarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CondicaoNaoEncontradaException.class,
                () -> useCase.consultarPorId(id));
    }

    @Test
    @DisplayName("Deve alterar condição existente (Atualiza dados e salva)")
    void deveAlterarComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Objeto que já existe no banco (Mockado)
        // Usamos spy ou mock para verificar se o setDados foi chamado,
        // ou usamos objetos reais se a classe Condicao for simples (POJO).
        Condicao condicaoExistente = spy(condicao);

        // Novos dados chegando

        // 1. O método alterar chama o consultarPorId primeiro
        when(gateway.consultarPorId(id)).thenReturn(Optional.of(condicaoExistente));

        // 2. Depois salva a condição existente atualizada
        when(gateway.salvar(condicaoExistente)).thenReturn(condicaoExistente);

        // Act
        Condicao result = useCase.alterar(id, condicao);

        // Assert
        assertNotNull(result);

        // Verifica se os dados foram copiados para o objeto existente
        verify(condicaoExistente).setDados(condicao);

        // Verifica se salvou o objeto correto
        verify(gateway).salvar(condicaoExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar ID inexistente")
    void deveFalharAoAlterarIdInexistente() {

        // Simula que não encontrou no banco
        when(gateway.consultarPorId(condicao.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CondicaoNaoEncontradaException.class,
                () -> useCase.alterar(condicao.getId(), condicao));

        // Garante que NUNCA tentou salvar se não achou
        verify(gateway, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve deletar condição existente")
    void deveDeletarComSucesso() {

        // Precisa existir para poder deletar (regra do seu método)
        when(gateway.consultarPorId(condicao.getId())).thenReturn(Optional.of(condicao));

        // Act
        useCase.deletar(condicao.getId());

        // Assert
        verify(gateway).consultarPorId(condicao.getId()); // Garante que validou
        verify(gateway).deletar(condicao.getId());        // Garante que deletou
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar ID inexistente")
    void deveFalharAoDeletarIdInexistente() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Simula que não encontrou
        when(gateway.consultarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CondicaoNaoEncontradaException.class,
                () -> useCase.deletar(id));

        // Garante que o delete NUNCA foi chamado no gateway
        verify(gateway, never()).deletar(any());
    }

}