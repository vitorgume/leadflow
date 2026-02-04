package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.NenhumVendedorReferenciadoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEscolhidoException;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes.CondicaoComposite;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes.CondicaoType;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscolhaVendedorUseCaseTest {

    @Mock
    private ConfiguracaoEscolhaVendedorUseCase configuracaoUseCase;

    @Mock
    private CondicaoComposite condicaoComposite;

    @Mock
    private VendedorUseCase vendedorUseCase;

    @Mock
    private CondicaoType condicaoStrategy; // Mock da estratégia retornada pelo composite

    @InjectMocks
    private EscolhaVendedorUseCase useCase;

    private Cliente cliente;
    private Usuario usuario;
    private final UUID ID_USUARIO = UUID.randomUUID();

    @BeforeEach
    void setup() {
        // Limpa o estado estático (ultimoVendedor) antes de cada teste para evitar efeitos colaterais
        ReflectionTestUtils.setField(EscolhaVendedorUseCase.class, "ultimoVendedor", null);

        usuario = Usuario.builder()
                .id(ID_USUARIO)
                .nome("nome teste")
                .telefone("00000000000")
                .senha("senhateste123")
                .email("emailteste@123")
                .telefoneConectado("00000000000")
                .atributosQualificacao(Map.of("teste", new Object()))
                .configuracaoCrm(
                        ConfiguracaoCrm.builder()
                                .crmType(CrmType.KOMMO)
                                .mapeamentoCampos(Map.of("teste", "teste"))
                                .idTagAtivo("id-teste")
                                .idTagAtivo("id-teste")
                                .idEtapaAtivos("id-teste")
                                .idEtapaInativos("id-teste")
                                .acessToken("acess-token-teste")
                                .build()
                )
                .mensagemDirecionamentoVendedor("mensagem-teste")
                .mensagemRecontatoG1("mensagem-teste")
                .whatsappToken("token-teste")
                .whatsappIdInstance("id-teste")
                .agenteApiKey("api-key-teste")
                .build();

        cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Nome domain")
                .telefone("00000000000")
                .atributosQualificacao(Map.of("teste", new Object()))
                .inativo(false)
                .usuario(usuario)
                .build();
    }

    @Test
    @DisplayName("Cenário 1: Deve retornar imediatamente se houver apenas 1 vendedor na conta")
    void deveRetornarUnicoVendedorDaConta() {
        // Arrange
        Vendedor vendedorUnico = new Vendedor();
        vendedorUnico.setNome("Vendedor Solitário");

        when(vendedorUseCase.listarPorUsuario(ID_USUARIO)).thenReturn(List.of(vendedorUnico));

        // Act
        Vendedor resultado = useCase.escolherVendedor(cliente);

        // Assert
        assertEquals("Vendedor Solitário", resultado.getNome());
        // Garante que nem buscou configurações
        verifyNoInteractions(configuracaoUseCase);
    }

    @Test
    @DisplayName("Cenário 2: Deve escolher vendedor baseado na configuração prioritária (Condições atendidas)")
    void deveEscolherVendedorPorConfiguracao() {
        // Arrange
        // Existem vários vendedores na conta
        when(vendedorUseCase.listarPorUsuario(ID_USUARIO)).thenReturn(List.of(new Vendedor(), new Vendedor()));

        // Config 1 (Prioridade 1): Atende as condições
        Vendedor vendedorAlvo = new Vendedor();
        vendedorAlvo.setNome("Vendedor Alvo");
        vendedorAlvo.setInativo(false);

        Condicao condicaoTrue = new Condicao();
        condicaoTrue.setOperadorLogico(OperadorLogico.EQUAL);

        ConfiguracaoEscolhaVendedor config1 = new ConfiguracaoEscolhaVendedor();
        config1.setId(UUID.randomUUID());
        config1.setPrioridade(1);
        config1.setCondicoes(List.of(condicaoTrue));
        config1.setVendedores(List.of(vendedorAlvo));

        // Mock das configs
        when(configuracaoUseCase.listarPorUsuario(ID_USUARIO)).thenReturn(List.of(config1));

        // Mock da avaliação de condição (Sempre True)
        when(condicaoComposite.escolher(OperadorLogico.EQUAL)).thenReturn(condicaoStrategy);
        when(condicaoStrategy.executar(eq(cliente), any())).thenReturn(true);

        // Act
        Vendedor resultado = useCase.escolherVendedor(cliente);

        // Assert
        assertEquals("Vendedor Alvo", resultado.getNome());
    }

    @Test
    @DisplayName("Cenário 3: Lógica Booleana (TRUE AND FALSE OR TRUE) -> Deve retornar True")
    void deveAvaliarLogicaComplexaCorretamente() {
        /*
           Lógica do teste:
           C1 (True) AND C2 (False) -> False
           OR
           C3 (True)
           Resultado esperado: True (Configuração aceita)
         */

        // Arrange
        // 1. Mock inicial para passar pela validação de vendedores da conta
        when(vendedorUseCase.listarPorUsuario(ID_USUARIO))
                .thenReturn(List.of(new Vendedor(), new Vendedor()));

        Vendedor vendedorFinal = new Vendedor();
        vendedorFinal.setInativo(false);
        vendedorFinal.setNome("Vendedor Escolhido");

        // 2. Configura as Condições
        Condicao c1 = new Condicao();
        c1.setOperadorLogico(OperadorLogico.EQUAL);
        c1.setConectorLogico(ConectorLogico.AND);

        Condicao c2 = new Condicao();
        c2.setOperadorLogico(OperadorLogico.NOT_EQUAL);
        c2.setConectorLogico(ConectorLogico.OR);

        Condicao c3 = new Condicao();
        c3.setOperadorLogico(OperadorLogico.IS_LESS_THAN); // <--- Definido como LESS_THAN
        c3.setConectorLogico(null);

        // 3. Configura a Regra de Negócio
        ConfiguracaoEscolhaVendedor config = new ConfiguracaoEscolhaVendedor();
        config.setId(UUID.randomUUID());
        config.setPrioridade(1);
        config.setCondicoes(List.of(c1, c2, c3));
        config.setVendedores(List.of(vendedorFinal));

        when(configuracaoUseCase.listarPorUsuario(ID_USUARIO)).thenReturn(List.of(config));

        // 4. Mock do Composite (Factory)
        // Precisamos garantir que os stubs batam EXATAMENTE com os operadores definidos acima
        when(condicaoComposite.escolher(OperadorLogico.EQUAL)).thenReturn(condicaoStrategy);
        when(condicaoComposite.escolher(OperadorLogico.NOT_EQUAL)).thenReturn(condicaoStrategy);

        // --- CORREÇÃO AQUI ---
        // Antes estava IS_GREATER_THAN, mas o objeto c3 usa IS_LESS_THAN. Agora batem!
        when(condicaoComposite.escolher(OperadorLogico.IS_LESS_THAN)).thenReturn(condicaoStrategy);

        // 5. Define os retornos da execução da estratégia
        // C1=True, C2=False, C3=True
        when(condicaoStrategy.executar(eq(cliente), eq(c1))).thenReturn(true);
        when(condicaoStrategy.executar(eq(cliente), eq(c2))).thenReturn(false);
        when(condicaoStrategy.executar(eq(cliente), eq(c3))).thenReturn(true);

        // Act
        Vendedor resultado = useCase.escolherVendedor(cliente);

        // Assert
        assertNotNull(resultado);
        assertEquals(vendedorFinal, resultado);
    }

    @Test
    @DisplayName("Cenário 4: Nenhuma configuração atende -> Deve lançar exceção")
    void deveLancarExcecaoSeNenhumaConfigAtender() {
        // Arrange
        when(vendedorUseCase.listarPorUsuario(ID_USUARIO)).thenReturn(List.of(new Vendedor(), new Vendedor()));

        // Configura uma condição que retorna FALSE
        ConfiguracaoEscolhaVendedor config = new ConfiguracaoEscolhaVendedor();
        config.setPrioridade(1);
        Condicao c1 = new Condicao();
        c1.setOperadorLogico(OperadorLogico.EQUAL);
        config.setCondicoes(List.of(c1));

        when(configuracaoUseCase.listarPorUsuario(ID_USUARIO)).thenReturn(List.of(config));

        when(condicaoComposite.escolher(any())).thenReturn(condicaoStrategy);
        when(condicaoStrategy.executar(any(), any())).thenReturn(false); // Condição falha

        // Act & Assert
        assertThrows(VendedorNaoEscolhidoException.class, () -> useCase.escolherVendedor(cliente));
    }

    @Test
    @DisplayName("Roleta: Deve evitar repetir o último vendedor escolhido")
    void deveEvitarRepetirUltimoVendedor() {
        // Arrange
        Vendedor v1 = new Vendedor(); v1.setNome("V1"); v1.setInativo(false);
        Vendedor v2 = new Vendedor(); v2.setNome("V2"); v2.setInativo(false);

        List<Vendedor> lista = List.of(v1, v2);

        // Simula que o V1 foi o último escolhido
        ReflectionTestUtils.setField(EscolhaVendedorUseCase.class, "ultimoVendedor", "V1");

        // Act
        // Como V1 foi o último e V2 está disponível, a roleta DEVE escolher V2
        Vendedor resultado = useCase.roletaVendedores(lista);

        // Assert
        assertEquals("V2", resultado.getNome());

        // Verifica se atualizou o estático
        String novoUltimo = (String) ReflectionTestUtils.getField(EscolhaVendedorUseCase.class, "ultimoVendedor");
        assertEquals("V2", novoUltimo);
    }

    @Test
    @DisplayName("Roleta: Se só sobrar o último vendedor ativo, repete ele mesmo")
    void deveRepetirVendedorSeForUnicoOpcao() {
        // Arrange
        Vendedor v1 = new Vendedor(); v1.setNome("V1"); v1.setInativo(false);
        List<Vendedor> lista = List.of(v1);

        ReflectionTestUtils.setField(EscolhaVendedorUseCase.class, "ultimoVendedor", "V1");

        // Act
        Vendedor resultado = useCase.roletaVendedores(lista);

        // Assert
        assertEquals("V1", resultado.getNome());
    }

    @Test
    @DisplayName("Roleta: Deve lançar exceção se todos os vendedores estiverem inativos")
    void deveLancarExcecaoSeTodosInativos() {
        // Arrange
        Vendedor v1 = new Vendedor(); v1.setInativo(true);
        List<Vendedor> lista = List.of(v1);

        // Act & Assert
        assertThrows(NenhumVendedorReferenciadoException.class, () -> useCase.roletaVendedores(lista));
    }

    @Test
    @DisplayName("Roleta Contatos Inativos: Fluxo completo de busca e escolha")
    void deveRodarRoletaContatosInativos() {
        // Arrange
        Vendedor v1 = new Vendedor(); v1.setNome("V1"); v1.setInativo(false);

        when(vendedorUseCase.listarPorUsuario(ID_USUARIO)).thenReturn(List.of(v1));

        // Act
        Vendedor resultado = useCase.roletaVendedoresContatosInativos(ID_USUARIO);

        // Assert
        assertEquals("V1", resultado.getNome());
        verify(vendedorUseCase).listarPorUsuario(ID_USUARIO);
    }

    @Test
    @DisplayName("Deve ordenar configurações por prioridade (Menor número primeiro)")
    void deveRespeitarPrioridadeConfiguracao() {
        // Arrange
        when(vendedorUseCase.listarPorUsuario(ID_USUARIO)).thenReturn(List.of(new Vendedor(), new Vendedor()));

        Vendedor vPrioridade1 = new Vendedor(); vPrioridade1.setNome("Vendedor P1"); vPrioridade1.setInativo(false);
        Vendedor vPrioridade2 = new Vendedor(); vPrioridade2.setNome("Vendedor P2"); vPrioridade2.setInativo(false);

        // Config P2 (Prioridade 2 - Baixa)
        ConfiguracaoEscolhaVendedor config2 = new ConfiguracaoEscolhaVendedor();
        config2.setPrioridade(2);
        config2.setVendedores(List.of(vPrioridade2));
        config2.setCondicoes(List.of(new Condicao())); // Condição fake

        // Config P1 (Prioridade 1 - Alta)
        ConfiguracaoEscolhaVendedor config1 = new ConfiguracaoEscolhaVendedor();
        config1.setPrioridade(1);
        config1.setVendedores(List.of(vPrioridade1));
        config1.setCondicoes(List.of(new Condicao())); // Condição fake

        // Retorna fora de ordem para testar o sort()
        when(configuracaoUseCase.listarPorUsuario(ID_USUARIO)).thenReturn(List.of(config2, config1));

        // Mock Strategy para retornar TRUE sempre
        when(condicaoComposite.escolher(any())).thenReturn(condicaoStrategy);
        when(condicaoStrategy.executar(any(), any())).thenReturn(true);

        // Act
        Vendedor resultado = useCase.escolherVendedor(cliente);

        // Assert
        // Deve pegar o da P1, pois foi ordenado e processado primeiro
        assertEquals("Vendedor P1", resultado.getNome());
    }

}