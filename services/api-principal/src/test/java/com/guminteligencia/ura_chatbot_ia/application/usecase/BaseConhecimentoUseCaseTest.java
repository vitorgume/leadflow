package com.guminteligencia.ura_chatbot_ia.application.usecase;

import static org.junit.jupiter.api.Assertions.*;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.LimiteDeUmBaseConhecimentoJaAtingidoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.BaseConhecimentoNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.BaseConhecimentoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.BaseConhecimento;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseConhecimentoUseCaseTest {

    @Mock
    private BaseConhecimentoGateway gateway;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private BaseConhecimentoUseCase useCase;

    private UUID idUsuario;
    private UUID idBaseConhecimento;
    private Usuario usuario;
    private BaseConhecimento baseConhecimento;

    @BeforeEach
    void setup() {
        idUsuario = UUID.randomUUID();
        idBaseConhecimento = UUID.randomUUID();

        usuario = Usuario.builder()
                .id(idUsuario)
                .build();

        baseConhecimento = BaseConhecimento.builder()
                .id(idBaseConhecimento)
                .usuario(usuario)
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar a base de conhecimento com sucesso quando estiver dentro do limite")
    void deveCadastrarBaseConhecimentoComSucesso() {
        // Arrange
        when(usuarioUseCase.consultarPorId(idUsuario)).thenReturn(usuario);
        when(gateway.listar(idUsuario)).thenReturn(List.of()); // Lista vazia (não atingiu o limite > 1)
        when(gateway.salvar(any(BaseConhecimento.class))).thenReturn(baseConhecimento);

        // Act
        BaseConhecimento resultado = useCase.cadastrar(baseConhecimento);

        // Assert
        assertSame(baseConhecimento, resultado);
        assertEquals(usuario, baseConhecimento.getUsuario()); // Garante que o usuário foi associado
        verify(usuarioUseCase).consultarPorId(idUsuario);
        verify(gateway).listar(idUsuario);
        verify(gateway).salvar(baseConhecimento);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar quando o limite de bases de conhecimento for atingido")
    void deveLancarExcecaoQuandoLimiteAtingido() {
        // Arrange
        when(usuarioUseCase.consultarPorId(idUsuario)).thenReturn(usuario);

        // Simula que o usuário já tem 2 bases cadastradas (tamanho > 1 conforme a lógica atual)
        List<BaseConhecimento> basesExistentes = List.of(mock(BaseConhecimento.class), mock(BaseConhecimento.class));
        when(gateway.listar(idUsuario)).thenReturn(basesExistentes);

        // Act & Assert
        assertThrows(LimiteDeUmBaseConhecimentoJaAtingidoException.class, () -> useCase.cadastrar(baseConhecimento));

        // Garante que não tentou salvar no banco
        verify(gateway, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve delegar a listagem de bases de conhecimento para o gateway")
    void deveListarBasesConhecimentoDoUsuario() {
        // Arrange
        List<BaseConhecimento> lista = List.of(baseConhecimento);
        when(gateway.listar(idUsuario)).thenReturn(lista);

        // Act
        List<BaseConhecimento> resultado = useCase.listar(idUsuario);

        // Assert
        assertSame(lista, resultado);
        verify(gateway).listar(idUsuario);
    }

    @Test
    @DisplayName("Deve alterar uma base de conhecimento existente com sucesso")
    void deveAlterarBaseConhecimentoQuandoEncontrada() {
        // Arrange
        BaseConhecimento novosDados = BaseConhecimento.builder().titulo("Novo Titulo").build();

        // Usamos spy() para verificar se setDados foi chamado no objeto existente
        BaseConhecimento existente = spy(BaseConhecimento.builder().id(idBaseConhecimento).build());

        when(gateway.consultarPorId(idBaseConhecimento)).thenReturn(Optional.of(existente));

        // O código do useCase retorna gateway.salvar(novosDados), então mockamos isso
        when(gateway.salvar(novosDados)).thenReturn(novosDados);

        // Act
        BaseConhecimento resultado = useCase.alterar(idBaseConhecimento, novosDados);

        // Assert
        assertSame(novosDados, resultado);
        verify(existente).setDados(novosDados); // Garante que atualizou os dados em memória
        verify(gateway).salvar(novosDados);     // Garante a chamada de persistência
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar uma base de conhecimento que não existe")
    void deveLancarExcecaoAoAlterarBaseNaoEncontrada() {
        // Arrange
        when(gateway.consultarPorId(idBaseConhecimento)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseConhecimentoNaoEncontradoException.class, () -> useCase.alterar(idBaseConhecimento, baseConhecimento));
        verify(gateway, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve deletar a base de conhecimento com sucesso")
    void deveDeletarBaseConhecimentoQuandoEncontrada() {
        // Arrange
        when(gateway.consultarPorId(idBaseConhecimento)).thenReturn(Optional.of(baseConhecimento));

        // Act & Assert
        assertDoesNotThrow(() -> useCase.deletar(idBaseConhecimento));
        verify(gateway).deletar(idBaseConhecimento);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar uma base de conhecimento que não existe")
    void deveLancarExcecaoAoDeletarBaseNaoEncontrada() {
        // Arrange
        when(gateway.consultarPorId(idBaseConhecimento)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseConhecimentoNaoEncontradoException.class, () -> useCase.deletar(idBaseConhecimento));
        verify(gateway, never()).deletar(any());
    }
}