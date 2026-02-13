package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ConfiguraCrmUsuarioNaoConfiguradaException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.crm.IntegracaoMoskitGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.CriptografiaJCAUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.ContatoMoskitDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.EntityCustomField;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.PayloadMoskit;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegracaoMoskitTest {

    @InjectMocks
    private IntegracaoMoskit integracaoMoskit;

    @Mock
    private IntegracaoMoskitGateway gateway;

    @Mock
    private CriptografiaJCAUseCase criptografiaJCAUseCase;

    @Captor
    private ArgumentCaptor<PayloadMoskit> payloadCaptor;

    @Captor
    private ArgumentCaptor<ContatoMoskitDto> contatoCaptor;

    // Mocks de dados
    private Vendedor vendedor;
    private Cliente cliente;
    private ConversaAgente conversaAgente;
    private ConfiguracaoCrm configuracaoCrm;
    private Usuario usuario;

    private final String TOKEN_CRIPTOGRAFADO = "token-criptografado";
    private final String TOKEN_REAL = "token-real";
    private final String CRM_URL = "https://api.moskit.com";

    @BeforeEach
    void setup() {
        // Inicializa objetos básicos para os testes
        configuracaoCrm = new ConfiguracaoCrm();
        configuracaoCrm.setAcessToken(TOKEN_CRIPTOGRAFADO);
        configuracaoCrm.setCrmUrl(CRM_URL);
        configuracaoCrm.setIdEtapaAtivos("100");
        configuracaoCrm.setIdEtapaInativos("200");

        // Configura mapeamento de campos
        Map<String, String> mapeamento = new HashMap<>();
        mapeamento.put("Interesse", "custom_field_123");
        configuracaoCrm.setMapeamentoCampos(mapeamento);

        usuario = new Usuario();
        usuario.setConfiguracaoCrm(configuracaoCrm);

        cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setNome("Cliente Teste");
        cliente.setTelefone("11999999999");

        // Atributos de qualificação do cliente
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("Interesse", "Carro Novo"); // Campo mapeado
        atributos.put("OutroCampo", "Valor Ignorado"); // Campo não mapeado
        cliente.setAtributosQualificacao(atributos);

        vendedor = new Vendedor();
        vendedor.setIdVendedorCrm(999);

        conversaAgente = new ConversaAgente();
        StatusConversa status = StatusConversa.INATIVO_G2; // Status Ativo (código 1 no exemplo do código original parece ser inativo/ativo dependendo da regra de negócio, ajustei conforme lógica)
        // OBS: Na lógica original: codigo == 1 ? idEtapaInativos : idEtapaAtivos.
        // Se 1 for "Finalizado/Inativo", ele pega o 200. Se não, pega o 100.
        conversaAgente.setStatus(status);
    }

    @Test
    @DisplayName("implementacao: Deve executar fluxo completo quando profile for 'dev'")
    void implementacao_ProfileDev_Sucesso() {
        // Arrange
        ReflectionTestUtils.setField(integracaoMoskit, "profile", "dev");

        when(criptografiaJCAUseCase.descriptografar(TOKEN_CRIPTOGRAFADO)).thenReturn(TOKEN_REAL);
        when(gateway.criarContato(any(), eq(TOKEN_REAL), eq(CRM_URL))).thenReturn(555); // ID do contato criado

        // Act
        integracaoMoskit.implementacao(vendedor, cliente, conversaAgente);

        // Assert

        // 1. Verifica criação do contato
        verify(gateway).criarContato(contatoCaptor.capture(), eq(TOKEN_REAL), eq(CRM_URL));
        ContatoMoskitDto contatoCriado = contatoCaptor.getValue();
        assertEquals("Cliente Teste", contatoCriado.getName());
        assertEquals("11999999999", contatoCriado.getPhones().get(0).getNumber());

        // 2. Verifica criação do negócio (Deal)
        verify(gateway).criarNegocio(payloadCaptor.capture(), eq(TOKEN_REAL), eq(CRM_URL));
        PayloadMoskit payload = payloadCaptor.getValue();

        assertEquals("Cliente Teste", payload.getName());
        assertEquals(555, payload.getContacts().get(0).getId()); // ID retornado pelo criarContato
        assertEquals("OPEN", payload.getStatus());

        // Verifica mapeamento de campos customizados
        assertFalse(payload.getEntityCustomFields().isEmpty());
        EntityCustomField customField = payload.getEntityCustomFields().get(0);
        assertEquals("custom_field_123", customField.getId()); // ID do CRM
        assertEquals("Carro Novo", customField.getTextValue()); // Valor do cliente

        // Verifica Stage (Etapa)
        // codigo 1 -> idEtapaInativos (200)
        assertEquals(200, payload.getStage().get("id"));
    }

    @Test
    @DisplayName("implementacao: Deve usar etapa de ATIVOS quando status conversa for diferente de 1")
    void implementacao_ProfileDev_EtapaAtivos() {
        // Arrange
        ReflectionTestUtils.setField(integracaoMoskit, "profile", "dev");
        conversaAgente.setStatus(StatusConversa.ATIVO); // Status diferente de 1

        when(criptografiaJCAUseCase.descriptografar(TOKEN_CRIPTOGRAFADO)).thenReturn(TOKEN_REAL);
        when(gateway.criarContato(any(), any(), any())).thenReturn(555);

        // Act
        integracaoMoskit.implementacao(vendedor, cliente, conversaAgente);

        // Assert
        verify(gateway).criarNegocio(payloadCaptor.capture(), any(), any());

        // codigo != 1 -> idEtapaAtivos (100)
        assertEquals(100, payloadCaptor.getValue().getStage().get("id"));
    }

    @Test
    @DisplayName("implementacao: Deve lançar exceção se configuração CRM for nula")
    void implementacao_ProfileDev_SemConfiguracao() {
        // Arrange
        ReflectionTestUtils.setField(integracaoMoskit, "profile", "dev");
        cliente.getUsuario().setConfiguracaoCrm(null);

        // Act & Assert
        assertThrows(ConfiguraCrmUsuarioNaoConfiguradaException.class,
                () -> integracaoMoskit.implementacao(vendedor, cliente, conversaAgente));

        verifyNoInteractions(gateway);
    }

    @Test
    @DisplayName("implementacao: Deve lançar exceção se mapeamento de campos for nulo")
    void implementacao_ProfileDev_SemMapeamento() {
        // Arrange
        ReflectionTestUtils.setField(integracaoMoskit, "profile", "dev");
        cliente.getUsuario().getConfiguracaoCrm().setMapeamentoCampos(null);

        // Act & Assert
        assertThrows(ConfiguraCrmUsuarioNaoConfiguradaException.class,
                () -> integracaoMoskit.implementacao(vendedor, cliente, conversaAgente));

        verifyNoInteractions(gateway);
    }

    @Test
    @DisplayName("implementacao: Não deve fazer nada se profile NÃO for 'dev'")
    void implementacao_ProfileProd() {
        // Arrange
        ReflectionTestUtils.setField(integracaoMoskit, "profile", "prod");

        // Act
        integracaoMoskit.implementacao(vendedor, cliente, conversaAgente);

        // Assert
        verifyNoInteractions(gateway);
        verifyNoInteractions(criptografiaJCAUseCase);
    }

    @Test
    @DisplayName("getCrmType: Deve retornar tipo MOSKIT")
    void getCrmType() {
        assertEquals(CrmType.MOSKIT, integracaoMoskit.getCrmType());
    }

}