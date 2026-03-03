package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.LimiteDeUmPromptJaAtingidoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.PromptNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.PromptGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Prompt;
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
class PromptUseCaseTest {

    @Mock
    private PromptGateway gateway;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private PromptUseCase useCase;

    private UUID idUsuario;
    private UUID idPrompt;
    private Usuario usuario;
    private Prompt prompt;

    @BeforeEach
    void setup() {
        idUsuario = UUID.randomUUID();
        idPrompt = UUID.randomUUID();

        usuario = Usuario.builder()
                .id(idUsuario)
                .build();

        prompt = Prompt.builder()
                .id(idPrompt)
                .usuario(usuario)
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar o prompt com sucesso quando estiver dentro do limite")
    void deveCadastrarPromptComSucesso() {
        // Arrange
        when(usuarioUseCase.consultarPorId(idUsuario)).thenReturn(usuario);
        when(gateway.listar(idUsuario)).thenReturn(List.of()); // Lista vazia (não atingiu limite)
        when(gateway.salvar(any(Prompt.class))).thenReturn(prompt);

        // Act
        Prompt resultado = useCase.cadastrar(prompt);

        // Assert
        assertSame(prompt, resultado);
        assertEquals(usuario, prompt.getUsuario()); // Garante que o usuario foi setado
        verify(usuarioUseCase).consultarPorId(idUsuario);
        verify(gateway).listar(idUsuario);
        verify(gateway).salvar(prompt);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar quando o limite de prompts for atingido")
    void deveLancarExcecaoQuandoLimiteAtingido() {
        // Arrange
        when(usuarioUseCase.consultarPorId(idUsuario)).thenReturn(usuario);

        // Simula que o usuário já tem 2 prompts (tamanho > 1 conforme a lógica atual)
        List<Prompt> promptsExistentes = List.of(mock(Prompt.class), mock(Prompt.class));
        when(gateway.listar(idUsuario)).thenReturn(promptsExistentes);

        // Act & Assert
        assertThrows(LimiteDeUmPromptJaAtingidoException.class, () -> useCase.cadastrar(prompt));

        // Garante que não tentou salvar no banco
        verify(gateway, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve delegar a listagem de prompts para o gateway")
    void deveListarPromptsDoUsuario() {
        // Arrange
        List<Prompt> lista = List.of(prompt);
        when(gateway.listar(idUsuario)).thenReturn(lista);

        // Act
        List<Prompt> resultado = useCase.listar(idUsuario);

        // Assert
        assertSame(lista, resultado);
        verify(gateway).listar(idUsuario);
    }

    @Test
    @DisplayName("Deve alterar um prompt existente com sucesso")
    void deveAlterarPromptQuandoEncontrado() {
        // Arrange
        Prompt novosDados = Prompt.builder().titulo("Novo Título").build();

        // Usamos spy() para podermos verificar se o método setDados foi chamado
        Prompt existente = spy(Prompt.builder().id(idPrompt).build());

        when(gateway.consultarPorId(idPrompt)).thenReturn(Optional.of(existente));
        when(gateway.salvar(novosDados)).thenReturn(novosDados);

        // Act
        Prompt resultado = useCase.alterar(idPrompt, novosDados);

        // Assert
        assertSame(novosDados, resultado);
        verify(existente).setDados(novosDados); // Garante que atualizou a instância encontrada
        verify(gateway).salvar(novosDados);     // Garante que chamou o salvar
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar um prompt que não existe")
    void deveLancarExcecaoAoAlterarPromptNaoEncontrado() {
        // Arrange
        when(gateway.consultarPorId(idPrompt)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PromptNaoEncontradoException.class, () -> useCase.alterar(idPrompt, prompt));
        verify(gateway, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve deletar o prompt com sucesso")
    void deveDeletarPromptQuandoEncontrado() {
        // Arrange
        when(gateway.consultarPorId(idPrompt)).thenReturn(Optional.of(prompt));

        // Act & Assert
        assertDoesNotThrow(() -> useCase.deletar(idPrompt));
        verify(gateway).deletar(idPrompt);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar um prompt que não existe")
    void deveLancarExcecaoAoDeletarPromptNaoEncontrado() {
        // Arrange
        when(gateway.consultarPorId(idPrompt)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PromptNaoEncontradoException.class, () -> useCase.deletar(idPrompt));
        verify(gateway, never()).deletar(any());
    }
}