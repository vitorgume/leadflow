package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.OutroContatoComMesmoTelefoneJaCadastradoExcetion;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OutroContatoUseCaseTest {

    @Mock
    private OutroContatoGateway gateway;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private OutroContatoUseCase outroContatoUseCase;

    private OutroContato outroContatoTeste;

    @BeforeEach
    void setUp() {
        outroContatoTeste = OutroContato.builder().id(1L).nome("Nome teste").build();
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
        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class))).thenReturn(Optional.of(outroContatoTeste));

        OutroContato resultadoTeste = outroContatoUseCase.consultarPorTipo(outroContatoTeste.getTipoContato(), UUID.randomUUID());

        Assertions.assertEquals(outroContatoTeste.getId(), resultadoTeste.getId());
        Mockito.verify(gateway, Mockito.times(1)).consultarPorTipo(Mockito.any(), Mockito.any(UUID.class));
    }

    @Test
    void deveLancarExceptionQuandoConsultarPorTipoRetornarVazia() {
        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class))).thenReturn(Optional.empty());

        OutroContatoNaoEncontradoException exception = Assertions
                .assertThrows(OutroContatoNaoEncontradoException.class,
                        () -> outroContatoUseCase.consultarPorTipo(outroContatoTeste.getTipoContato(), UUID.randomUUID())
                );

        assertEquals("Outro contato não encontrado.", exception.getMessage());
    }

    @Test
    void deveCadastrarComSucesso() {
        outroContatoTeste.setUsuario(Usuario.builder().id(UUID.randomUUID()).build());
        outroContatoTeste.setTelefone("123456789");
        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class))).thenReturn(Optional.empty());
        Mockito.when(gateway.consultarPorTelefoneEUsuario(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(usuarioUseCase.consultarPorId(Mockito.any(UUID.class))).thenReturn(new Usuario());
        Mockito.when(gateway.salvar(Mockito.any(OutroContato.class))).thenReturn(outroContatoTeste);

        OutroContato resultadoTeste = outroContatoUseCase.cadastrar(outroContatoTeste);

        Assertions.assertEquals(outroContatoTeste.getId(), resultadoTeste.getId());
        Mockito.verify(gateway, Mockito.times(1)).salvar(Mockito.any(OutroContato.class));
    }

    @Test
    void deveLancarExceptionQuandoCadastrarGerenteJaExistente() {
        outroContatoTeste.setTipoContato(TipoContato.GERENTE);
        outroContatoTeste.setUsuario(Usuario.builder().id(UUID.randomUUID()).build());

        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class))).thenReturn(Optional.of(outroContatoTeste));

        OutroContatoTipoGerenciaJaCadastradoException exception = Assertions
                .assertThrows(OutroContatoTipoGerenciaJaCadastradoException.class,
                        () -> outroContatoUseCase.cadastrar(outroContatoTeste)
                );

        assertEquals("Outro contato do tipo gerencia já cadastrado.", exception.getMessage());
    }

    @Test
    void deveLancarExceptionQuandoCadastrarTelefoneJaExistente() {
        outroContatoTeste.setUsuario(Usuario.builder().id(UUID.randomUUID()).build());
        outroContatoTeste.setTelefone("123456789");
        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class))).thenReturn(Optional.empty());
        Mockito.when(gateway.consultarPorTelefoneEUsuario(Mockito.anyString())).thenReturn(Optional.of(outroContatoTeste));

        OutroContatoComMesmoTelefoneJaCadastradoExcetion exception = Assertions
                .assertThrows(OutroContatoComMesmoTelefoneJaCadastradoExcetion.class,
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
        outroContatoTeste.setUsuario(Usuario.builder().id(UUID.randomUUID()).build());
        outroContatoTeste.setTelefone("123456789");
        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class))).thenReturn(Optional.empty());
        Mockito.when(gateway.consultarPorTelefoneEUsuario(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(gateway.consultarPorId(Mockito.anyLong())).thenReturn(Optional.of(outroContatoTeste));
        Mockito.when(gateway.salvar(Mockito.any(OutroContato.class))).thenReturn(outroContatoTeste);

        OutroContato resultado = outroContatoUseCase.alterar(1L, outroContatoTeste);

        Assertions.assertEquals(outroContatoTeste.getId(), resultado.getId());
        Mockito.verify(gateway, Mockito.times(1)).salvar(Mockito.any(OutroContato.class));
    }

    @Test
    void deveLancarExceptionQuandoAlterarGerenteJaExistente() {
        // 1. Prepara os novos dados que o frontend está enviando (para o ID 1L)
        OutroContato novosDados = OutroContato.builder()
                .tipoContato(TipoContato.GERENTE)
                .usuario(Usuario.builder().id(UUID.randomUUID()).build())
                .build();

        // 2. Simula um contato que JÁ EXISTE no banco de dados com OUTRO ID (2L)
        OutroContato contatoExistenteNoBanco = OutroContato.builder()
                .id(2L) // ID diferente de 1L para cair na sua nova validação
                .tipoContato(TipoContato.GERENTE)
                .build();

        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class)))
                .thenReturn(Optional.of(contatoExistenteNoBanco));

        OutroContatoTipoGerenciaJaCadastradoException exception = Assertions
                .assertThrows(OutroContatoTipoGerenciaJaCadastradoException.class,
                        () -> outroContatoUseCase.alterar(1L, novosDados)
                );

        assertEquals("Outro contato do tipo gerencia já cadastrado.", exception.getMessage());
    }

    @Test
    void deveLancarExceptionQuandoAlterarTelefoneJaExistente() {
        // 1. Prepara os novos dados da requisição
        OutroContato novosDados = OutroContato.builder()
                .telefone("123456789")
                .usuario(Usuario.builder().id(UUID.randomUUID()).build())
                .build();

        // 2. Simula o contato do banco que já é dono desse telefone, com um ID diferente (2L)
        OutroContato contatoExistenteNoBanco = OutroContato.builder()
                .id(2L) // ID diferente de 1L para ativar a exceção
                .telefone("123456789")
                .build();

        Mockito.when(gateway.consultarPorTipo(Mockito.any(), Mockito.any(UUID.class)))
                .thenReturn(Optional.empty()); // Passa liso pela primeira validação de gerente
        Mockito.when(gateway.consultarPorTelefoneEUsuario(Mockito.anyString()))
                .thenReturn(Optional.of(contatoExistenteNoBanco));

        OutroContatoComMesmoTelefoneJaCadastradoExcetion exception = Assertions
                .assertThrows(OutroContatoComMesmoTelefoneJaCadastradoExcetion.class,
                        () -> outroContatoUseCase.alterar(1L, novosDados)
                );

        assertEquals("Outro contato com o mesmo telefone já cadastrado.", exception.getMessage());
    }

    @Test
    void deveDeletarComSucesso() {
        Mockito.when(gateway.consultarPorId(Mockito.anyLong())).thenReturn(Optional.of(outroContatoTeste));

        outroContatoUseCase.deletar(1L);

        Mockito.verify(gateway, Mockito.times(1)).deletar(Mockito.anyLong());
    }

    @Test
    void deveLancarExceptionQuandoDeletarContatoNaoExistente() {
        Mockito.when(gateway.consultarPorId(Mockito.anyLong())).thenReturn(Optional.empty());

        OutroContatoNaoEncontradoException exception = Assertions
                .assertThrows(OutroContatoNaoEncontradoException.class,
                        () -> outroContatoUseCase.deletar(1L)
                );

        assertEquals("Outro contato não encontrado.", exception.getMessage());
    }
}