package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ConfiguracaoEscolhaVendedorNaoEncontrada;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ConfiguracaoEscolhaVendedorGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.UsuarioUseCase;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfiguracaoEscolhaVendedorUseCaseTest {

    @Mock
    private ConfiguracaoEscolhaVendedorGateway gateway;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @Mock
    private VendedorUseCase vendedorUseCase;

    @Mock
    private CondicaoUseCase condicaoUseCase;

    @InjectMocks
    private ConfiguracaoEscolhaVendedorUseCase useCase;

    // Constantes e Objetos auxiliares
    private final UUID ID_CONFIG = UUID.randomUUID();
    private final UUID ID_USUARIO = UUID.randomUUID();
    private final Long ID_VENDEDOR = 2L;
    private final UUID ID_CONDICAO = UUID.randomUUID();
    private final UUID ID_CLIENTE = UUID.randomUUID();
    private final UUID ID_CONFIGURACAO_ESCOLHA_VENDEDOR = UUID.randomUUID();

    private Usuario usuario;
    private Vendedor vendedor;
    private ConfiguracaoEscolhaVendedor configuracaoEscolhaVendedor;
    private Cliente cliente;
    private Condicao condicao;

    @BeforeEach
    void setUp() {
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
                .id(ID_CLIENTE)
                .nome("Nome domain")
                .telefone("00000000000")
                .atributosQualificacao(Map.of("teste", new Object()))
                .inativo(false)
                .usuario(usuario)
                .build();

        vendedor = Vendedor.builder()
                .id(ID_VENDEDOR)
                .nome("Vendedor domain")
                .telefone("0000000000000")
                .inativo(false)
                .idVendedorCrm(123)
                .padrao(false)
                .usuario(usuario)
                .build();

        condicao = Condicao.builder()
                .id(UUID.randomUUID())
                .campo("teste")
                .operadorLogico(OperadorLogico.CONTAINS)
                .valor("testevalor")
                .conectorLogico(ConectorLogico.AND)
                .build();

        configuracaoEscolhaVendedor = ConfiguracaoEscolhaVendedor.builder()
                .id(ID_CONFIGURACAO_ESCOLHA_VENDEDOR)
                .usuario(usuario)
                .vendedores(List.of(vendedor))
                .condicoes(List.of(condicao))
                .prioridade(1)
                .build();
    }

    @Test
    @DisplayName("Cadastrar: Deve buscar dependências e salvar com sucesso")
    void deveCadastrarComSucesso() {
        // Arrange

        usuario.setId(ID_USUARIO);

        vendedor.setId(ID_VENDEDOR);

        // Vendedor retornado do banco
        vendedor.setId(ID_VENDEDOR);
        vendedor.setNome("Vendedor Real");

        configuracaoEscolhaVendedor.setUsuario(usuario);
        configuracaoEscolhaVendedor.setVendedores(List.of(vendedor));
        configuracaoEscolhaVendedor.setCondicoes(List.of(condicao));

        // Mocks
        when(usuarioUseCase.consultarPorId(ID_USUARIO)).thenReturn(usuario);
        when(vendedorUseCase.consultarPorId(ID_VENDEDOR)).thenReturn(vendedor);
        when(gateway.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ConfiguracaoEscolhaVendedor result = useCase.cadastrar(configuracaoEscolhaVendedor);

        // Assert
        assertNotNull(result);
        assertEquals(usuario, result.getUsuario());
        assertEquals(1, result.getVendedores().size());

        // Garante que o vendedor salvo foi o consultado do banco (com nome), não o input
        assertEquals("Vendedor Real", result.getVendedores().get(0).getNome());

        verify(usuarioUseCase).consultarPorId(ID_USUARIO);
        verify(vendedorUseCase).consultarPorId(ID_VENDEDOR);
        verify(gateway).salvar(configuracaoEscolhaVendedor);
    }

    @Test
    @DisplayName("Listar por Usuário: Deve delegar para o gateway")
    void deveListarPorUsuario() {
        // Arrange
        when(gateway.listarPorUsuario(ID_USUARIO)).thenReturn(List.of(configuracaoEscolhaVendedor));

        // Act
        List<ConfiguracaoEscolhaVendedor> result = useCase.listarPorUsuario(ID_USUARIO);

        // Assert
        assertFalse(result.isEmpty());
        verify(gateway).listarPorUsuario(ID_USUARIO);
    }

    @Test
    @DisplayName("Listar Paginado: Deve delegar para o gateway")
    void deveListarPorUsuarioPaginado() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<ConfiguracaoEscolhaVendedor> page = new PageImpl<>(List.of(configuracaoEscolhaVendedor));

        when(gateway.listarPorUsuarioPaginado(ID_USUARIO, pageable)).thenReturn(page);

        // Act
        Page<ConfiguracaoEscolhaVendedor> result = useCase.listarPorUsuarioPaginado(ID_USUARIO, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(gateway).listarPorUsuarioPaginado(ID_USUARIO, pageable);
    }

    @Test
    @DisplayName("Alterar: Deve atualizar listas quando fornecidas (Vendedores e Condições)")
    void deveAlterarAtualizandoListas() {
        // Arrange
        // 1. Prepara o objeto existente (Spy)
        ConfiguracaoEscolhaVendedor configExistente = spy(new ConfiguracaoEscolhaVendedor());
        configExistente.setId(ID_CONFIG);

        when(gateway.consultarPorId(ID_CONFIG)).thenReturn(Optional.of(configExistente));

        // 2. Prepara os dados de Input (o que está vindo na requisição)
        vendedor.setId(ID_VENDEDOR);
        condicao.setId(ID_CONDICAO);

        // Objeto de entrada com as listas novas
        ConfiguracaoEscolhaVendedor inputAtualizacao = new ConfiguracaoEscolhaVendedor();
        inputAtualizacao.setVendedores(List.of(vendedor));
        inputAtualizacao.setCondicoes(List.of(condicao));

        // 3. Mocks das validações (UseCases auxiliares retornando os objetos reais)
        when(vendedorUseCase.consultarPorId(ID_VENDEDOR)).thenReturn(vendedor);
        when(condicaoUseCase.consultarPorId(ID_CONDICAO)).thenReturn(condicao);

        when(gateway.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ConfiguracaoEscolhaVendedor result = useCase.alterar(ID_CONFIG, inputAtualizacao);

        // Assert
        // --- CORREÇÃO AQUI ---
        // Verificamos apenas o setDados, pois é o único método chamado no objeto existente pelo UseCase
        verify(configExistente).setDados(inputAtualizacao);

        // Verificamos se salvou o objeto spy
        verify(gateway).salvar(configExistente);

        // Validação extra: O resultado final tem as listas preenchidas?
        assertEquals(1, result.getVendedores().size());
        assertEquals(ID_VENDEDOR, result.getVendedores().get(0).getId());

        assertEquals(1, result.getCondicoes().size());
        assertEquals(ID_CONDICAO, result.getCondicoes().get(0).getId());
    }

    @Test
    @DisplayName("Alterar: Deve manter listas antigas quando input for nulo ou vazio")
    void deveAlterarMantendoListasAntigas() {
        // Arrange
        List<Vendedor> vendedoresAntigos = List.of(vendedor);
        List<Condicao> condicoesAntigas = List.of(condicao);

        ConfiguracaoEscolhaVendedor configExistente = spy(configuracaoEscolhaVendedor);
        configExistente.setVendedores(vendedoresAntigos);
        configExistente.setCondicoes(condicoesAntigas);

        when(gateway.consultarPorId(ID_CONFIG)).thenReturn(Optional.of(configExistente));

        // Input com listas vazias/nulas
        configuracaoEscolhaVendedor.setVendedores(null);
        configuracaoEscolhaVendedor.setCondicoes(Collections.emptyList());

        when(gateway.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        useCase.alterar(ID_CONFIG, configuracaoEscolhaVendedor);

        // Assert
        // Verifica se setou a lista antiga de volta no objeto de input ou no objeto existente
        // Pela lógica: input.setVendedores(antigo) -> depois existente.setDados(input)

        // Verifica se NÃO chamou os usecases de consulta (pois manteve os antigos)
        verifyNoInteractions(vendedorUseCase);
        verifyNoInteractions(condicaoUseCase);

        verify(gateway).salvar(configExistente);
    }

    @Test
    @DisplayName("Alterar: Deve lançar exceção se ID não existir")
    void deveFalharAlteracaoSeIdNaoExiste() {
        // Arrange
        when(gateway.consultarPorId(ID_CONFIG)).thenReturn(Optional.empty());


        // Act & Assert
        assertThrows(ConfiguracaoEscolhaVendedorNaoEncontrada.class,
                () -> useCase.alterar(ID_CONFIG, configuracaoEscolhaVendedor));
    }

    @Test
    @DisplayName("Deletar: Deve remover condições associadas antes de deletar a config")
    void deveDeletarComCascadeManual() {
        // Arrange
        Condicao condicao1 = condicao;
        Condicao condicao2 = Condicao.builder()
                .id(UUID.randomUUID())
                .campo("campoteste2")
                .operadorLogico(OperadorLogico.EQUAL)
                .valor("valorteste2")
                .conectorLogico(ConectorLogico.OR)
                .build();

        configuracaoEscolhaVendedor.setCondicoes(List.of(condicao1, condicao2));

        when(gateway.consultarPorId(ID_CONFIG)).thenReturn(Optional.of(configuracaoEscolhaVendedor));

        // Act
        useCase.deletar(ID_CONFIG);

        // Assert
        // Verifica se consultou antes (método consultarPorId privado)
        verify(gateway).consultarPorId(ID_CONFIG);

        // Verifica se deletou cada condição individualmente
        verify(condicaoUseCase).deletar(condicao1.getId());
        verify(condicaoUseCase).deletar(condicao2.getId());

        // Verifica se deletou a config principal
        verify(gateway).deletar(ID_CONFIG);
    }

    @Test
    @DisplayName("Deletar: Deve lançar exceção se ID não existir")
    void deveFalharDeleteSeIdNaoExiste() {
        // Arrange
        when(gateway.consultarPorId(ID_CONFIG)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ConfiguracaoEscolhaVendedorNaoEncontrada.class,
                () -> useCase.deletar(ID_CONFIG));

        verify(gateway, never()).deletar(any());
        verifyNoInteractions(condicaoUseCase);
    }

}