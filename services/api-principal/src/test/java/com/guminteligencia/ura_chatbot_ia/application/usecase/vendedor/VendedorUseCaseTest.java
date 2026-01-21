package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorComMesmoTelefoneException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEscolhidoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.VendedorGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConfiguracaoEscolhaVendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes.CondicaoComposite;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes.CondicaoType;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
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

    @InjectMocks
    private VendedorUseCase useCase;

    private Usuario usuario;
    private Cliente cliente;
    private Vendedor vendedor1, vendedor2;

    @BeforeEach
    void setUp() throws Exception {
        Field f = VendedorUseCase.class.getDeclaredField("ultimoVendedor");
        f.setAccessible(true);
        f.set(null, null);

        usuario = Usuario.builder().id(UUID.randomUUID()).build();
        cliente = Cliente.builder()
                .usuario(usuario)
                .atributosQualificacao(Map.of(
                        "cidade", "São Paulo",
                        "valor_compra", 150,
                        "produto", "sapato"
                ))
                .build();
        vendedor1 = Vendedor.builder().id(1L).nome("João").build();
        vendedor2 = Vendedor.builder().id(2L).nome("Maria").build();
    }

    @Test
    @DisplayName("Deve lançar VendedorNaoEscolhidoException quando nenhuma configuração corresponder")
    void escolherVendedor_deveLancarException_quandoNenhumaConfiguracaoCorresponder() {
        when(configuracaoEscolhaVendedorUseCase.listarPorUsuario(usuario.getId())).thenReturn(Collections.emptyList());

        assertThrows(VendedorNaoEscolhidoException.class, () -> useCase.escolherVendedor(cliente));
    }

    @Test
    @DisplayName("Deve escolher vendedor com uma única condição AND correspondente")
    void escolherVendedor_deveEscolherVendedor_comCondicaoUnica() {
        Condicao condicao = new Condicao(UUID.randomUUID(), "cidade", OperadorLogico.EQUAL, "São Paulo", null);
        ConfiguracaoEscolhaVendedor config = new ConfiguracaoEscolhaVendedor(UUID.randomUUID(), usuario, vendedor1, List.of(condicao));
        
        mockCondicao(condicao, true);
        when(configuracaoEscolhaVendedorUseCase.listarPorUsuario(usuario.getId())).thenReturn(List.of(config));

        Vendedor resultado = useCase.escolherVendedor(cliente);

        assertEquals(vendedor1, resultado);
    }

    @Test
    @DisplayName("Deve escolher vendedor com condições 'true AND true'")
    void escolherVendedor_deveEscolherVendedor_comDuasCondicoesAndVerdadeiras() {
        Condicao c1 = new Condicao(UUID.randomUUID(), "cidade", OperadorLogico.EQUAL, "São Paulo", ConectorLogico.AND);
        Condicao c2 = new Condicao(UUID.randomUUID(), "valor_compra", OperadorLogico.IS_GREATER_THAN, "100", null);
        ConfiguracaoEscolhaVendedor config = new ConfiguracaoEscolhaVendedor(UUID.randomUUID(), usuario, vendedor1, List.of(c1, c2));

        mockCondicao(c1, true);
        mockCondicao(c2, true);
        when(configuracaoEscolhaVendedorUseCase.listarPorUsuario(usuario.getId())).thenReturn(List.of(config));

        Vendedor resultado = useCase.escolherVendedor(cliente);

        assertEquals(vendedor1, resultado);
    }

    @Test
    @DisplayName("Não deve escolher vendedor com condições 'true AND false'")
    void escolherVendedor_naoDeveEscolherVendedor_comUmaCondicaoAndFalsa() {
        Condicao c1 = new Condicao(UUID.randomUUID(), "cidade", OperadorLogico.EQUAL, "São Paulo", ConectorLogico.AND);
        Condicao c2 = new Condicao(UUID.randomUUID(), "valor_compra", OperadorLogico.IS_LESS_THAN, "100", null);
        ConfiguracaoEscolhaVendedor config = new ConfiguracaoEscolhaVendedor(UUID.randomUUID(), usuario, vendedor1, List.of(c1, c2));

        mockCondicao(c1, true);
        mockCondicao(c2, false);
        when(configuracaoEscolhaVendedorUseCase.listarPorUsuario(usuario.getId())).thenReturn(List.of(config));

        assertThrows(VendedorNaoEscolhidoException.class, () -> useCase.escolherVendedor(cliente));
    }

    @Test
    @DisplayName("Deve escolher vendedor com condições 'false OR true'")
    void escolherVendedor_deveEscolherVendedor_comCondicaoOrVerdadeira() {
        Condicao c1 = new Condicao(UUID.randomUUID(), "cidade", OperadorLogico.EQUAL, "Rio de Janeiro", ConectorLogico.OR);
        Condicao c2 = new Condicao(UUID.randomUUID(), "valor_compra", OperadorLogico.IS_GREATER_THAN, "100", null);
        ConfiguracaoEscolhaVendedor config = new ConfiguracaoEscolhaVendedor(UUID.randomUUID(), usuario, vendedor1, List.of(c1, c2));

        mockCondicao(c1, false);
        mockCondicao(c2, true);
        when(configuracaoEscolhaVendedorUseCase.listarPorUsuario(usuario.getId())).thenReturn(List.of(config));

        Vendedor resultado = useCase.escolherVendedor(cliente);

        assertEquals(vendedor1, resultado);
    }

    @Test
    @DisplayName("Deve escolher vendedor com precedência de AND: 'false OR true AND true'")
    void escolherVendedor_deveRespeitarPrecedenciaDoAnd() {
        Condicao c1 = new Condicao(UUID.randomUUID(), "produto", OperadorLogico.EQUAL, "calça", ConectorLogico.OR);
        Condicao c2 = new Condicao(UUID.randomUUID(), "cidade", OperadorLogico.EQUAL, "São Paulo", ConectorLogico.AND);
        Condicao c3 = new Condicao(UUID.randomUUID(), "valor_compra", OperadorLogico.IS_GREATER_THAN, "140", null);
        ConfiguracaoEscolhaVendedor config = new ConfiguracaoEscolhaVendedor(UUID.randomUUID(), usuario, vendedor1, List.of(c1, c2, c3));

        mockCondicao(c1, false); // produto == 'calça' -> false
        mockCondicao(c2, true);  // cidade == 'São Paulo' -> true
        mockCondicao(c3, true);  // valor_compra > 140 -> true
        when(configuracaoEscolhaVendedorUseCase.listarPorUsuario(usuario.getId())).thenReturn(List.of(config));

        // A expressão é: false OR true AND true. Deve resultar em true.
        Vendedor resultado = useCase.escolherVendedor(cliente);

        assertEquals(vendedor1, resultado);
    }

     @Test
    @DisplayName("Não deve escolher vendedor com precedência de AND: 'true AND false OR false'")
    void escolherVendedor_deveFalharComPrecedenciaDoAnd() {
        Condicao c1 = new Condicao(UUID.randomUUID(), "cidade", OperadorLogico.EQUAL, "São Paulo", ConectorLogico.AND);
        Condicao c2 = new Condicao(UUID.randomUUID(), "produto", OperadorLogico.EQUAL, "calça", ConectorLogico.OR);
        Condicao c3 = new Condicao(UUID.randomUUID(), "valor_compra", OperadorLogico.IS_GREATER_THAN, "200", null);
        ConfiguracaoEscolhaVendedor config = new ConfiguracaoEscolhaVendedor(UUID.randomUUID(), usuario, vendedor1, List.of(c1, c2, c3));

        mockCondicao(c1, true);   // cidade == 'São Paulo' -> true
        mockCondicao(c2, false);  // produto == 'calça' -> false
        mockCondicao(c3, false);  // valor_compra > 200 -> false
        when(configuracaoEscolhaVendedorUseCase.listarPorUsuario(usuario.getId())).thenReturn(List.of(config));

        // A expressão é: true AND false OR false. Deve resultar em false.
        assertThrows(VendedorNaoEscolhidoException.class, () -> useCase.escolherVendedor(cliente));
    }


    private void mockCondicao(Condicao condicao, boolean resultado) {
        CondicaoType mockCondicaoType = mock(CondicaoType.class);
        when(condicaoComposite.escolher(condicao.getOperadorLogico())).thenReturn(mockCondicaoType);
        when(mockCondicaoType.executar(eq(cliente), eq(condicao))).thenReturn(resultado);
    }

    // Manter os outros testes que ainda são válidos
    @Test
    void cadastrarDeveLancarExceptionQuandoTelefoneJaExiste() {
        Vendedor novo = Vendedor.builder().telefone("+5511999").build();
        Vendedor existente = Vendedor.builder().telefone("+5511999").build();
        when(gateway.consultarPorTelefone("+5511999"))
                .thenReturn(Optional.of(existente));

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
    void listarDeveDelegarParaGateway() {
        List<Vendedor> lista = List.of(Vendedor.builder().nome("A").build());
        when(gateway.listar()).thenReturn(lista);
        List<Vendedor> res = useCase.listar();
        assertSame(lista, res);
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