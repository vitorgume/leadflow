package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ClienteNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ClienteGateway;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ObjetoRelatorioDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class ClienteUseCaseTest {

    @Mock
    private ClienteGateway gateway;
    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private ClienteUseCase useCase;

    private final String telefone = "+5511999000111";
    private final String telefoneUsuario = "5511999999999";
    private UUID idCliente;
    private Cliente clienteSalvo;
    private Usuario usuario;

    @BeforeEach
    void setup() {
        idCliente = UUID.randomUUID();
        clienteSalvo = Cliente.builder()
                .id(idCliente)
                .telefone(telefone)
                .build();

        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .telefone(telefoneUsuario)
                .build();
    }

    @Test
    void deveDelegarConsultarPorTelefone() {
        when(usuarioUseCase.consultarPorTelefoneConectado(telefoneUsuario)).thenReturn(usuario);
        when(gateway.consultarPorTelefoneEUsuario(telefone, usuario.getId()))
                .thenReturn(Optional.of(clienteSalvo));

        Optional<Cliente> resultado = useCase.consultarPorTelefoneEUsuario(telefone, telefoneUsuario);

        assertTrue(resultado.isPresent());
        assertSame(clienteSalvo, resultado.get());
        verify(gateway).consultarPorTelefoneEUsuario(telefone, usuario.getId());
    }

    @Test
    void deveCadastrarClienteComTelefone() {
        when(usuarioUseCase.consultarPorTelefoneConectado(telefoneUsuario)).thenReturn(usuario);
        when(gateway.salvar(any(Cliente.class))).thenReturn(clienteSalvo);

        Cliente resultado = useCase.cadastrar(telefone, telefoneUsuario);

        assertSame(clienteSalvo, resultado);
        ArgumentCaptor<Cliente> cap = ArgumentCaptor.forClass(Cliente.class);
        verify(gateway).salvar(cap.capture());
        assertEquals(telefone, cap.getValue().getTelefone());
        assertEquals(usuario, cap.getValue().getUsuario());
    }

    @Test
    void deveAlterarClienteQuandoEncontrado() {
        Cliente novosDados = Cliente.builder()
                .nome("NovoNome")
                .telefone("+552211223344")
                .build();

        Cliente existente = spy(Cliente.builder()
                .id(idCliente)
                .telefone(telefone)
                .build());
        when(gateway.consultarPorId(idCliente))
                .thenReturn(Optional.of(existente));
        when(gateway.salvar(existente)).thenReturn(existente);

        Cliente resultado = useCase.alterar(novosDados, idCliente);

        assertSame(existente, resultado);
        verify(existente).setDados(novosDados);
        verify(gateway).salvar(existente);
    }

    @Test
    void deveLancarQuandoAlterarQuandoNaoEncontrado() {
        when(gateway.consultarPorId(idCliente))
                .thenReturn(Optional.empty());

        assertThrows(
                ClienteNaoEncontradoException.class,
                () -> useCase.alterar(Cliente.builder().build(), idCliente)
        );
        verify(gateway).consultarPorId(idCliente);
        verify(gateway, never()).salvar(any());
    }

    @Test
    void deveRetornarRelatorioSegundaFeiraDelegandoAoGateway() {
        List<ObjetoRelatorioDto> rel = List.of(mock(ObjetoRelatorioDto.class));
        when(gateway.getRelatorioContato(usuario.getId())).thenReturn(rel);

        List<ObjetoRelatorioDto> resultado = useCase.getRelatorioSegundaFeira(usuario.getId());

        assertSame(rel, resultado);
        verify(gateway).getRelatorioContato(usuario.getId());
    }

    @Test
    void deveRetornarRelatorioDelegandoAoGateway() {
        List<ObjetoRelatorioDto> rel = List.of(mock(ObjetoRelatorioDto.class));
        when(gateway.getRelatorioContatoSegundaFeira(usuario.getId())).thenReturn(rel);

        List<ObjetoRelatorioDto> resultado = useCase.getRelatorio(usuario.getId());

        assertSame(rel, resultado);
        verify(gateway).getRelatorioContatoSegundaFeira(usuario.getId());
    }

}