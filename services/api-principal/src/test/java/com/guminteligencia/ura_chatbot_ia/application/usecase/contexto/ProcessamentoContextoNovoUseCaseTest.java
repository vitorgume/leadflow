package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente.ProcessamentoContextoExistente;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessamentoContextoNovoUseCaseTest {

    @Mock
    private ClienteUseCase clienteUseCase;

    @Mock
    private ConversaAgenteUseCase conversaAgenteUseCase;

    @Mock
    private ProcessamentoContextoExistente processamentoContextoExistente;

    @InjectMocks
    private ProcessamentoContextoNovoUseCase useCase;

    private Cliente cliente;
    private ConversaAgente conversaAgente;
    private Usuario usuario;

    // Mocks de dados comuns
    private final String TELEFONE_CLIENTE = "5511999999999";
    private final String TELEFONE_USUARIO = "5511888888888";

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(UUID.randomUUID())
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

        conversaAgente = ConversaAgente.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .vendedor(Vendedor.builder().id(1L).usuario(Usuario.builder().id(UUID.randomUUID()).configuracaoCrm(ConfiguracaoCrm.builder().crmType(CrmType.KOMMO).build()).build()).build())
                .dataCriacao(LocalDateTime.now())
                .finalizada(false)
                .dataUltimaMensagem(LocalDateTime.now().plusHours(1))
                .recontato(false)
                .build();
    }

    @Test
    @DisplayName("Cenário 1: Cliente JÁ EXISTE - Deve recuperar cliente, criar conversa e processar")
    void deveProcessarComClienteExistente() {
        // Arrange
        Contexto contexto = new Contexto();
        contexto.setTelefone(TELEFONE_CLIENTE);
        contexto.setTelefoneUsuario(TELEFONE_USUARIO);

        cliente.setTelefone(TELEFONE_CLIENTE);

        // Mock: Cliente é encontrado
        when(clienteUseCase.consultarPorTelefoneEUsuario(TELEFONE_CLIENTE, TELEFONE_USUARIO))
                .thenReturn(Optional.of(cliente));

        // Mock: Criação de conversa
        when(conversaAgenteUseCase.criar(cliente)).thenReturn(conversaAgente);

        // Act
        useCase.processarContextoNovo(contexto);

        // Assert
        // Verifica se consultou
        verify(clienteUseCase).consultarPorTelefoneEUsuario(TELEFONE_CLIENTE, TELEFONE_USUARIO);

        // CRUCIAL: Verifica se NÃO tentou cadastrar um novo cliente (pois já existia)
        verify(clienteUseCase, never()).cadastrar(any(), any());

        // Verifica fluxo restante
        verify(conversaAgenteUseCase).criar(cliente);
        verify(conversaAgenteUseCase).salvar(conversaAgente);
        verify(processamentoContextoExistente).processarContextoExistente(cliente, contexto);
    }

    @Test
    @DisplayName("Cenário 2: Cliente NÃO EXISTE - Deve cadastrar novo cliente, criar conversa e processar")
    void deveProcessarCriandoNovoCliente() {
        // Arrange
        Contexto contexto = new Contexto();
        contexto.setTelefone(TELEFONE_CLIENTE);
        contexto.setTelefoneUsuario(TELEFONE_USUARIO);

        cliente.setTelefone(TELEFONE_CLIENTE);

        // Mock: Cliente NÃO é encontrado (Empty)
        when(clienteUseCase.consultarPorTelefoneEUsuario(TELEFONE_CLIENTE, TELEFONE_USUARIO))
                .thenReturn(Optional.empty());

        // Mock: Simula o cadastro retornando o novo cliente
        when(clienteUseCase.cadastrar(TELEFONE_CLIENTE, TELEFONE_USUARIO))
                .thenReturn(cliente);

        when(conversaAgenteUseCase.criar(cliente)).thenReturn(conversaAgente);

        // Act
        useCase.processarContextoNovo(contexto);

        // Assert
        // Verifica se consultou
        verify(clienteUseCase).consultarPorTelefoneEUsuario(TELEFONE_CLIENTE, TELEFONE_USUARIO);

        // CRUCIAL: Verifica se O MÉTODO CADASTRAR FOI CHAMADO
        verify(clienteUseCase).cadastrar(TELEFONE_CLIENTE, TELEFONE_USUARIO);

        // Verifica fluxo restante com o NOVO cliente
        verify(conversaAgenteUseCase).criar(cliente);
        verify(conversaAgenteUseCase).salvar(conversaAgente);
        verify(processamentoContextoExistente).processarContextoExistente(cliente, contexto);
    }

    @Test
    @DisplayName("Cenário 3: Erro ao Consultar Cliente - Deve propagar exceção e parar fluxo")
    void devePropagarErroAoConsultarCliente() {
        // Arrange
        Contexto contexto = new Contexto();
        contexto.setTelefone(TELEFONE_CLIENTE);
        contexto.setTelefoneUsuario(TELEFONE_USUARIO);

        when(clienteUseCase.consultarPorTelefoneEUsuario(any(), any()))
                .thenThrow(new RuntimeException("Erro banco de dados"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> useCase.processarContextoNovo(contexto));

        // Garante que o fluxo parou antes de tentar criar conversa
        verify(conversaAgenteUseCase, never()).criar(any());
        verify(processamentoContextoExistente, never()).processarContextoExistente(any(), any());
    }

    @Test
    @DisplayName("Cenário 4: Erro ao Cadastrar Cliente - Deve propagar exceção")
    void devePropagarErroAoCadastrarCliente() {
        // Arrange
        Contexto contexto = new Contexto();
        contexto.setTelefone(TELEFONE_CLIENTE);
        contexto.setTelefoneUsuario(TELEFONE_USUARIO);

        // Simula cliente não encontrado
        when(clienteUseCase.consultarPorTelefoneEUsuario(any(), any())).thenReturn(Optional.empty());

        // Simula erro no cadastro
        when(clienteUseCase.cadastrar(any(), any()))
                .thenThrow(new RuntimeException("Erro ao salvar cliente"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> useCase.processarContextoNovo(contexto));

        // Garante que não avançou para conversa
        verify(conversaAgenteUseCase, never()).criar(any());
    }

    @Test
    @DisplayName("Cenário 5: Erro ao Processar Contexto Existente - Deve propagar exceção")
    void devePropagarErroAoProcessarContextoExistente() {
        // Arrange
        Contexto contexto = new Contexto();
        contexto.setTelefone(TELEFONE_CLIENTE);
        contexto.setTelefoneUsuario(TELEFONE_USUARIO);

        when(clienteUseCase.consultarPorTelefoneEUsuario(any(), any())).thenReturn(Optional.of(cliente));
        when(conversaAgenteUseCase.criar(cliente)).thenReturn(conversaAgente);

        // Simula erro na última etapa
        doThrow(new RuntimeException("Erro processamento final"))
                .when(processamentoContextoExistente).processarContextoExistente(cliente, contexto);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> useCase.processarContextoNovo(contexto));

        // Verifica que tudo anterior foi chamado
        verify(conversaAgenteUseCase).salvar(conversaAgente);
        // Mas o método principal falhou
        verify(processamentoContextoExistente).processarContextoExistente(cliente, contexto);
    }
}