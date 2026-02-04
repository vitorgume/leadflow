package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorComMesmoTelefoneException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEscolhidoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.VendedorGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes.CondicaoComposite;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes.CondicaoType;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.application.usecase.UsuarioUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConectorLogico;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConfiguracaoEscolhaVendedor;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendedorUseCaseTest {

    @Mock
    private VendedorGateway gateway;

    @Mock
    private ConfiguracaoEscolhaVendedorUseCase configuracaoEscolhaVendedorUseCase;
    @Mock
    private CondicaoComposite condicaoComposite;
    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private VendedorUseCase useCase;

    private Usuario usuario;
    private Cliente cliente;
    private Vendedor vendedor1, vendedor2;


//

//
    @Test
    void cadastrarDeveLancarExceptionQuandoTelefoneJaExiste() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = Usuario.builder().id(usuarioId).build();
        Vendedor novo = Vendedor.builder().telefone("+5511999").usuario(usuario).build();
        Vendedor existente = Vendedor.builder().telefone("+5511999").build();

        when(gateway.consultarPorTelefone("+5511999"))
                .thenReturn(Optional.of(existente));
        lenient().when(usuarioUseCase.consultarPorId(usuarioId)).thenReturn(usuario);

        assertThrows(
                VendedorComMesmoTelefoneException.class,
                () -> useCase.cadastrar(novo),
                "Telefones iguais devem disparar exceção"
        );

        verify(gateway).consultarPorTelefone("+5511999");
        verify(gateway, never()).salvar(any());
    }

    @Test
    void consultarVendedorRetornaQuandoEncontrado() {
        Vendedor v = Vendedor.builder().nome("Z").build();
        when(gateway.consultarVendedor("Z")).thenReturn(Optional.of(v));

        Vendedor res = useCase.consultarVendedor("Z");
        assertSame(v, res);
    }

    @Test
    void consultarVendedorLancaExceptionQuandoNaoEncontrado() {
        when(gateway.consultarVendedor("N")).thenReturn(Optional.empty());
        assertThrows(
                VendedorNaoEncontradoException.class,
                () -> useCase.consultarVendedor("N")
        );
    }

    @Test
    void alterarDeveChamarSalvarERetornar() {
        Vendedor orig = Vendedor.builder().id(1L).build();
        Vendedor novos = Vendedor.builder().id(1L).nome("Novo").build();
        when(gateway.consultarPorId(1L)).thenReturn(Optional.of(orig));
        when(gateway.salvar(orig)).thenReturn(novos);

        Vendedor res = useCase.alterar(novos, 1L);
        assertEquals("Novo", res.getNome());
        verify(gateway).consultarPorId(1L);
        verify(gateway).salvar(orig);
    }

    @Test
    void listarPorUsuarioDeveDelegarParaGateway() {
        UUID idUsuario = UUID.randomUUID();
        List<Vendedor> lista = List.of(Vendedor.builder().nome("A").build());
        when(gateway.listarPorUsuario(idUsuario)).thenReturn(lista);
        List<Vendedor> res = useCase.listarPorUsuario(idUsuario);
        assertSame(lista, res);
        verify(gateway).listarPorUsuario(idUsuario);
    }

    @Test
    void deletarDeveChamarConsultarEDeletar() {
        when(gateway.consultarPorId(3L))
                .thenReturn(Optional.of(Vendedor.builder().id(3L).build()));
        useCase.deletar(3L);
        verify(gateway).consultarPorId(3L);
        verify(gateway).deletar(3L);
    }
}