package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.OutroContatoComMesmoTelefoneJaCadastradoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.OutroContatoNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.OutroContatoTipoGerenciaJaCadastradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.OutroContatoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.TipoContato;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OutroContatoUseCaseTest {

    @Mock
    private OutroContatoGateway gateway;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    // NOVO: Adicionado o mock para o NoSQL
    @Mock
    private OutroContatoNoSqlUseCase outroContatoNoSqlUseCase;

    @InjectMocks
    private OutroContatoUseCase outroContatoUseCase;

    private OutroContato outroContatoTeste;

    @BeforeEach
    void setUp() {
        // Ajustado para sempre ter um telefone e usuario, já que o NoSQL usa ambos nas buscas/deleções
        outroContatoTeste = OutroContato.builder()
                .id(UUID.randomUUID())
                .nome("Nome teste")
                .telefone("11999999999")
                .usuario(Usuario.builder().id(UUID.randomUUID()).build())
                .tipoContato(TipoContato.PADRAO)
                .build();
    }

    @Test
    void deveConsultarPorNomeComSucesso() {
        Mockito.when(gateway.consultarPorNome(Mockito.anyString())).thenReturn(Optional.of(outroContatoTeste));

        OutroContato resultadoTeste = outroContatoUseCase.consultarPorNome(outroContatoTeste.getNome());

        Assertions.assertEquals(outroContatoTeste.getId(), resultadoTeste.getId());
        Mockito.verify(gateway, Mockito.times(1)).consultarPorNome(Mockito.anyString());
    }

    @Test
    void deveLancarExceptionQuandoConsultarRetornarVazia() {
        Mockito.when(gateway.consultarPorNome(Mockito.anyString())).thenReturn(Optional.empty());

        OutroContatoNaoEncontradoException exception = Assertions
                .assertThrows(OutroContatoNaoEncontradoException.class,
                        () -> outroContatoUseCase.consultarPorNome(outroContatoTeste.getNome())
                );

        assertEquals("Outro contato não encontrado.", exception.getMessage());
    }

    @Test
    void deveConsultarPorTipoComSucesso() {
        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class))).thenReturn(new ArrayList<>(List.of(outroContatoTeste)));

        List<OutroContato> resultadoTeste = outroContatoUseCase.consultarPorTipo(outroContatoTeste.getTipoContato(), UUID.randomUUID());

        Assertions.assertEquals(outroContatoTeste.getId(), resultadoTeste.get(0).getId());
        Mockito.verify(gateway, Mockito.times(1)).consultarPorTipo(Mockito.any(), Mockito.any(UUID.class));
    }

    @Test
    void deveCadastrarComSucesso() {
        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class))).thenReturn(new ArrayList<>());
        Mockito.when(gateway.consultarPorTelefoneEUsuario(Mockito.anyString(), Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(usuarioUseCase.consultarPorId(Mockito.any(UUID.class))).thenReturn(new Usuario());
        Mockito.when(gateway.salvar(Mockito.any(OutroContato.class))).thenReturn(outroContatoTeste);

        OutroContato resultadoTeste = outroContatoUseCase.cadastrar(outroContatoTeste);

        Assertions.assertEquals(outroContatoTeste.getId(), resultadoTeste.getId());
        Mockito.verify(gateway, Mockito.times(1)).salvar(Mockito.any(OutroContato.class));
        // NOVO: Verificando se também salvou no NoSQL
        Mockito.verify(outroContatoNoSqlUseCase, Mockito.times(1)).salvar(Mockito.any(OutroContato.class));
    }

    @Test
    void deveLancarExceptionQuandoCadastrarGerenteJaExistente() {
        outroContatoTeste.setTipoContato(TipoContato.GERENTE);

        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class))).thenReturn(new ArrayList<>(List.of(outroContatoTeste)));

        OutroContatoTipoGerenciaJaCadastradoException exception = Assertions
                .assertThrows(OutroContatoTipoGerenciaJaCadastradoException.class,
                        () -> outroContatoUseCase.cadastrar(outroContatoTeste)
                );

        assertEquals("Outro contato do tipo gerência já cadastrado.", exception.getMessage());
    }

    @Test
    void deveLancarExceptionQuandoCadastrarTelefoneJaExistente() {
        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class))).thenReturn(List.of());
        Mockito.when(gateway.consultarPorTelefoneEUsuario(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(outroContatoTeste));

        OutroContatoComMesmoTelefoneJaCadastradoException exception = Assertions
                .assertThrows(OutroContatoComMesmoTelefoneJaCadastradoException.class,
                        () -> outroContatoUseCase.cadastrar(outroContatoTeste)
                );

        assertEquals("Outro contato com o mesmo telefone já cadastrado.", exception.getMessage());
    }

    @Test
    void deveListarComSucesso() {
        Page<OutroContato> page = Page.empty();
        Mockito.when(gateway.listar(Mockito.any(Pageable.class), Mockito.any(UUID.class))).thenReturn(page);

        Page<OutroContato> resultado = outroContatoUseCase.listar(Pageable.unpaged(), UUID.randomUUID());

        Assertions.assertEquals(page, resultado);
        Mockito.verify(gateway, Mockito.times(1)).listar(Mockito.any(Pageable.class), Mockito.any(UUID.class));
    }

    @Test
    void deveAlterarComSucesso() {
        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class))).thenReturn(new ArrayList<>());
        Mockito.when(gateway.consultarPorTelefoneEUsuario(Mockito.anyString(), Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(gateway.consultarPorId(Mockito.any())).thenReturn(Optional.of(outroContatoTeste));

        // NOVO: Mockando a consulta do DynamoDB para o fluxo de alteração
        Mockito.when(outroContatoNoSqlUseCase.consultarPorTelefoneEUsuario(Mockito.anyString(), Mockito.any(UUID.class)))
                .thenReturn(outroContatoTeste);

        Mockito.when(gateway.salvar(Mockito.any(OutroContato.class))).thenReturn(outroContatoTeste);

        OutroContato resultado = outroContatoUseCase.alterar(UUID.randomUUID(), outroContatoTeste);

        Assertions.assertEquals(outroContatoTeste.getId(), resultado.getId());
        Mockito.verify(gateway, Mockito.times(1)).salvar(Mockito.any(OutroContato.class));
        // NOVO: Verificando se também atualizou no NoSQL
        Mockito.verify(outroContatoNoSqlUseCase, Mockito.times(1)).salvar(Mockito.any(OutroContato.class));
    }

    @Test
    void deveLancarExceptionQuandoAlterarGerenteJaExistente() {
        OutroContato novosDados = OutroContato.builder()
                .tipoContato(TipoContato.GERENTE)
                .usuario(Usuario.builder().id(UUID.randomUUID()).build())
                .build();

        OutroContato contatoExistenteNoBanco = OutroContato.builder()
                .id(UUID.randomUUID())
                .tipoContato(TipoContato.GERENTE)
                .build();

        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class)))
                .thenReturn(new ArrayList<>(List.of(contatoExistenteNoBanco)));

        OutroContatoTipoGerenciaJaCadastradoException exception = Assertions
                .assertThrows(OutroContatoTipoGerenciaJaCadastradoException.class,
                        () -> outroContatoUseCase.alterar(UUID.randomUUID(), novosDados)
                );

        assertEquals("Outro contato do tipo gerência já cadastrado.", exception.getMessage());
    }

    @Test
    void deveLancarExceptionQuandoAlterarTelefoneJaExistente() {
        OutroContato novosDados = OutroContato.builder()
                .telefone("123456789")
                .usuario(Usuario.builder().id(UUID.randomUUID()).build())
                .tipoContato(TipoContato.PADRAO)
                .build();

        OutroContato contatoExistenteNoBanco = OutroContato.builder()
                .id(UUID.randomUUID())
                .telefone("123456789")
                .build();

        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class)))
                .thenReturn(new ArrayList<>());
        Mockito.when(gateway.consultarPorTelefoneEUsuario(Mockito.anyString(), Mockito.any()))
                .thenReturn(Optional.of(contatoExistenteNoBanco));

        OutroContatoComMesmoTelefoneJaCadastradoException exception = Assertions
                .assertThrows(OutroContatoComMesmoTelefoneJaCadastradoException.class,
                        () -> outroContatoUseCase.alterar(UUID.randomUUID(), novosDados)
                );

        assertEquals("Outro contato com o mesmo telefone já cadastrado.", exception.getMessage());
    }

    @Test
    void deveDeletarComSucesso() {
        Mockito.when(gateway.consultarPorId(Mockito.any())).thenReturn(Optional.of(outroContatoTeste));

        outroContatoUseCase.deletar(UUID.randomUUID());

        Mockito.verify(gateway, Mockito.times(1)).deletar(Mockito.any());
        // NOVO: Verificando se o deletr do NoSQL foi chamado passando os argumentos corretos
        Mockito.verify(outroContatoNoSqlUseCase, Mockito.times(1)).deletar(Mockito.anyString(), Mockito.any(UUID.class));
    }

    @Test
    void deveLancarExceptionQuandoDeletarContatoNaoExistente() {
        Mockito.when(gateway.consultarPorId(Mockito.any())).thenReturn(Optional.empty());

        OutroContatoNaoEncontradoException exception = Assertions
                .assertThrows(OutroContatoNaoEncontradoException.class,
                        () -> outroContatoUseCase.deletar(UUID.randomUUID())
                );

        assertEquals("Outro contato não encontrado.", exception.getMessage());
    }
}