package com.guminteligencia.ura_chatbot_ia.application.usecase;

import static org.junit.jupiter.api.Assertions.*;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.MembroComMesmoNumeroJaCadastradoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.MembroNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.MembroGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class MembroUseCaseTest {

    @Mock
    private MembroGateway gateway;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private MembroUseCase membroUseCase;

    private Membro membroTeste;
    private Usuario usuarioTeste;
    private final UUID ID_MEMBRO = UUID.randomUUID();
    private final UUID ID_USUARIO = UUID.randomUUID();
    private final String TELEFONE = "5511999999999";

    @BeforeEach
    void setUp() {
        usuarioTeste = new Usuario();
        usuarioTeste.setId(ID_USUARIO);

        membroTeste = Membro.builder()
                .id(ID_MEMBRO)
                .nome("João Silva")
                .telefone(TELEFONE)
                .usuario(usuarioTeste)
                .build();
    }

    @Test
    @DisplayName("Cadastrar: Deve cadastrar membro com sucesso")
    void deveCadastrarMembroComSucesso() {
        Mockito.when(gateway.consultarPorTelefone(TELEFONE)).thenReturn(Optional.empty());
        Mockito.when(usuarioUseCase.consultarPorId(ID_USUARIO)).thenReturn(usuarioTeste);
        Mockito.when(gateway.salvar(Mockito.any(Membro.class))).thenReturn(membroTeste);

        Membro resultado = membroUseCase.cadastrar(membroTeste);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(membroTeste.getNome(), resultado.getNome());
        Mockito.verify(gateway, Mockito.times(1)).salvar(membroTeste);
    }

    @Test
    @DisplayName("Cadastrar: Deve lançar exception quando telefone já existe")
    void deveLancarExceptionAoCadastrarComTelefoneJaExistente() {
        Mockito.when(gateway.consultarPorTelefone(TELEFONE)).thenReturn(Optional.of(membroTeste));

        Assertions.assertThrows(MembroComMesmoNumeroJaCadastradoException.class,
                () -> membroUseCase.cadastrar(membroTeste));

        Mockito.verify(gateway, Mockito.never()).salvar(Mockito.any());
    }

    @Test
    @DisplayName("Listar: Deve retornar lista de membros do usuário")
    void deveListarMembros() {
        List<Membro> membros = Collections.singletonList(membroTeste);
        Mockito.when(gateway.listar(ID_USUARIO)).thenReturn(membros);

        List<Membro> resultado = membroUseCase.listar(ID_USUARIO);

        Assertions.assertEquals(membros, resultado);
        Mockito.verify(gateway, Mockito.times(1)).listar(ID_USUARIO);
    }

    @Test
    @DisplayName("Alterar: Deve alterar membro com sucesso quando o telefone não mudou")
    void deveAlterarMembroComSucessoQuandoTelefoneEhOMesmo() {
        // Usamos spy para monitorar se o método setDados será chamado
        Membro membroExistente = Mockito.spy(membroTeste);

        Membro novosDados = Membro.builder()
                .nome("Nome Editado")
                .telefone(TELEFONE) // Mesmo telefone
                .build();

        Mockito.when(gateway.consultarPorId(ID_MEMBRO)).thenReturn(Optional.of(membroExistente));
        Mockito.when(gateway.salvar(membroExistente)).thenReturn(membroExistente);

        Membro resultado = membroUseCase.alterar(novosDados, ID_MEMBRO);

        Assertions.assertNotNull(resultado);
        // Verifica se a consulta por telefone foi pulada, já que o número é o mesmo
        Mockito.verify(gateway, Mockito.never()).consultarPorTelefone(Mockito.anyString());
        // Garante que injetou os novos dados na entidade gerenciada e salvou
        Mockito.verify(membroExistente).setDados(novosDados);
        Mockito.verify(gateway).salvar(membroExistente);
    }

    @Test
    @DisplayName("Alterar: Deve alterar membro com sucesso quando o telefone mudou e está disponível")
    void deveAlterarMembroComSucessoQuandoTelefoneEhDiferenteENaoExiste() {
        Membro membroExistente = Mockito.spy(membroTeste);
        String novoTelefone = "5511888888888";

        Membro novosDados = Membro.builder()
                .nome("Nome Editado")
                .telefone(novoTelefone) // Telefone diferente
                .build();

        Mockito.when(gateway.consultarPorId(ID_MEMBRO)).thenReturn(Optional.of(membroExistente));
        // Como o telefone mudou, a validação de disponibilidade deve rodar e retornar vazio
        Mockito.when(gateway.consultarPorTelefone(novoTelefone)).thenReturn(Optional.empty());
        Mockito.when(gateway.salvar(membroExistente)).thenReturn(membroExistente);

        Membro resultado = membroUseCase.alterar(novosDados, ID_MEMBRO);

        Assertions.assertNotNull(resultado);
        Mockito.verify(gateway).consultarPorTelefone(novoTelefone);
        Mockito.verify(membroExistente).setDados(novosDados);
        Mockito.verify(gateway).salvar(membroExistente);
    }

    @Test
    @DisplayName("Alterar: Deve lançar exception quando o novo telefone já pertence a outro membro")
    void deveLancarExceptionAoAlterarQuandoTelefoneNovoJaExiste() {
        Membro membroExistente = membroTeste;
        String novoTelefone = "5511888888888";

        Membro novosDados = Membro.builder()
                .nome("Nome Editado")
                .telefone(novoTelefone)
                .build();

        Membro membroConcorrente = Membro.builder().id(UUID.randomUUID()).telefone(novoTelefone).build();

        Mockito.when(gateway.consultarPorId(ID_MEMBRO)).thenReturn(Optional.of(membroExistente));
        // O telefone novo já existe no banco
        Mockito.when(gateway.consultarPorTelefone(novoTelefone)).thenReturn(Optional.of(membroConcorrente));

        Assertions.assertThrows(MembroComMesmoNumeroJaCadastradoException.class,
                () -> membroUseCase.alterar(novosDados, ID_MEMBRO));

        Mockito.verify(gateway, Mockito.never()).salvar(Mockito.any());
    }

    @Test
    @DisplayName("Alterar: Deve lançar exception quando membro não for encontrado")
    void deveLancarExceptionAoAlterarMembroNaoExistente() {
        Membro novosDados = Membro.builder().build();

        Mockito.when(gateway.consultarPorId(ID_MEMBRO)).thenReturn(Optional.empty());

        Assertions.assertThrows(MembroNaoEncontradoException.class,
                () -> membroUseCase.alterar(novosDados, ID_MEMBRO));

        Mockito.verify(gateway, Mockito.never()).salvar(Mockito.any());
    }

    @Test
    @DisplayName("Deletar: Deve deletar membro com sucesso")
    void deveDeletarMembroComSucesso() {
        Mockito.when(gateway.consultarPorId(ID_MEMBRO)).thenReturn(Optional.of(membroTeste));

        membroUseCase.deletar(ID_MEMBRO);

        Mockito.verify(gateway, Mockito.times(1)).deletar(ID_MEMBRO);
    }

    @Test
    @DisplayName("Deletar: Deve lançar exception ao tentar deletar membro não existente")
    void deveLancarExceptionAoDeletarMembroNaoExistente() {
        Mockito.when(gateway.consultarPorId(ID_MEMBRO)).thenReturn(Optional.empty());

        Assertions.assertThrows(MembroNaoEncontradoException.class,
                () -> membroUseCase.deletar(ID_MEMBRO));

        Mockito.verify(gateway, Mockito.never()).deletar(Mockito.any());
    }
}