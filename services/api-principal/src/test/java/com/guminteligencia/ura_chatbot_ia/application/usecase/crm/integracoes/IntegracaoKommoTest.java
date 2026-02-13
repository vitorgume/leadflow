package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ConfiguraCrmUsuarioNaoConfiguradaException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.LeadNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.crm.IntegracaoKommoGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.CriptografiaJCAUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.kommo.PayloadKommo;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegracaoKommoTest {

    @Mock
    private IntegracaoKommoGateway gateway;

    @Mock
    private CriptografiaJCAUseCase criptografiaJCAUseCase;

    @InjectMocks
    private IntegracaoKommo integracaoKommo;

    // Dados para setup
    private Vendedor vendedor;
    private Cliente cliente;
    private ConversaAgente conversaAgente;
    private ConfiguracaoCrm configCrm;
    private Usuario usuario;

    @BeforeEach
    void setup() {
        // Configura o Profile padrão como "prod" para a maioria dos testes
        ReflectionTestUtils.setField(integracaoKommo, "profile", "prod");

        vendedor = Vendedor.builder()
                .id(1L)
                .nome("Vendedor domain")
                .telefone("0000000000000")
                .inativo(false)
                .idVendedorCrm(123)
                .padrao(false)
                .usuario(
                        Usuario.builder()
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
                                .build()
                )
                .build();

        vendedor.setIdVendedorCrm(999);

        // Configuração do CRM simulada
        configCrm = ConfiguracaoCrm.builder()
                .acessToken("token-encriptado")
                .idTagAtivo("100")
                .idTagInativo("200")
                .idEtapaAtivos("10")
                .idEtapaInativos("20")
                .crmUrl("http://test.com")
                // Mapeia "campo_local" -> ID 555 no CRM
                .mapeamentoCampos(Map.of("segmento", "555", "regiao", "666"))
                .build();

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

        usuario.setConfiguracaoCrm(configCrm);

        // Cliente com dados para envio
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("segmento", "Varejo"); // Mapeado
        atributos.put("regiao", "Sul");      // Mapeado
        atributos.put("ignore_me", "X");     // Não mapeado (deve ser ignorado)

        cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Nome domain")
                .telefone("00000000000")
                .atributosQualificacao(Map.of("teste", new Object()))
                .inativo(false)
                .usuario(usuario)
                .build();

        cliente.setTelefone("5511999999999");
        cliente.setUsuario(usuario);
        cliente.setAtributosQualificacao(atributos);

        conversaAgente = ConversaAgente.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .vendedor(vendedor)
                .dataCriacao(LocalDateTime.now())
                .finalizada(false)
                .dataUltimaMensagem(LocalDateTime.now().plusHours(1))
                .recontato(false)
                .build();
    }

    @Test
    @DisplayName("Cenário Feliz: Profile PROD, Status ATIVO, Lead Encontrado")
    void deveAtualizarCrmComSucessoEmProd() {
        // Arrange
        conversaAgente.setStatus(StatusConversa.ATIVO); // Supondo que código seja 0 ou 2 (ajuste conforme seu Enum)
        // Se seu Enum StatusConversa.ATIVO tiver código 0 ou 2:
        // Mockamos o comportamento do status para garantir o teste da lógica
        // (Ou use o Enum real se ele já tiver os códigos corretos)

        // Mock Criptografia
        when(criptografiaJCAUseCase.descriptografar("token-encriptado")).thenReturn("token-real");

        // Mock Lead Encontrado (ID 12345)
        when(gateway.consultaLeadPeloTelefone("5511999999999", "token-real", "http://test.com"))
                .thenReturn(Optional.of(12345));

        // Act
        integracaoKommo.implementacao(vendedor, cliente, conversaAgente);

        // Assert
        ArgumentCaptor<PayloadKommo> captor = ArgumentCaptor.forClass(PayloadKommo.class);
        verify(gateway).atualizarCard(captor.capture(), eq(12345), eq("token-real"), eq("http://test.com"));

        PayloadKommo payload = captor.getValue();

        // 1. Verifica IDs básicos
        assertEquals(999, payload.getResponsibleUserId());

        // 2. Verifica Lógica de Status (Ativo -> idEtapaAtivos = 10)
        // O código diz: status != 1 -> idEtapaAtivos
        assertEquals(10, payload.getStatusId());

        // 3. Verifica Tags (Ativo -> idTagAtivo = 100)
        // O código diz: status == 2 ou 0 -> tagAtivo
        @SuppressWarnings("unchecked")
        Map<String, Object> embedded = (Map<String, Object>) payload.getEmbedded();
        List<Map<String, Integer>> tags = (List<Map<String, Integer>>) embedded.get("tags");
        assertEquals(100, tags.get(0).get("id"));

        // 4. Verifica Custom Fields (Mapeamento)
        assertEquals(2, payload.getCustomFieldsValues().size()); // Só segmento e regiao, ignorou o extra

        // Verifica se mapeou segmento -> 555
        boolean achouSegmento = payload.getCustomFieldsValues().stream()
                .anyMatch(f -> f.getFieldId() == 555 && f.getValues().get(0).getValue().equals("Varejo"));
        assertTrue(achouSegmento);
    }

    @Test
    @DisplayName("Cenário Inativo: Verifica troca de Tags e Etapas")
    void deveUsarIdsInativosQuandoStatusForFinalizado() {
        // Arrange
        // Simulando status código 1 (INATIVO/FINALIZADO)
        // Precisamos garantir que conversaAgente.getStatus().getCodigo() retorne 1
        // Se for Enum, set o Enum correto. Aqui vou simular com Mock para forçar o caminho 1
        StatusConversa statusMock = mock(StatusConversa.class);
        when(statusMock.getCodigo()).thenReturn(1);

        ConversaAgente conversaMock = mock(ConversaAgente.class);
        when(conversaMock.getStatus()).thenReturn(statusMock);

        // Configurações básicas
        when(criptografiaJCAUseCase.descriptografar(any())).thenReturn("token");
        when(gateway.consultaLeadPeloTelefone(any(), any(), any())).thenReturn(Optional.of(1));

        // Act
        integracaoKommo.implementacao(vendedor, cliente, conversaMock);

        // Assert
        ArgumentCaptor<PayloadKommo> captor = ArgumentCaptor.forClass(PayloadKommo.class);
        verify(gateway).atualizarCard(captor.capture(), any(), any(), any());
        PayloadKommo payload = captor.getValue();

        // Status 1 -> Deve usar idEtapaInativos (20)
        assertEquals(20, payload.getStatusId());

        // Status 1 (não é 0 nem 2) -> Deve usar idTagInativo (200)
        @SuppressWarnings("unchecked")
        Map<String, Object> embedded = (Map<String, Object>) payload.getEmbedded();
        List<Map<String, Integer>> tags = (List<Map<String, Integer>>) embedded.get("tags");
        assertEquals(200, tags.get(0).get("id"));
    }

    @Test
    @DisplayName("Não deve fazer nada se profile não for PROD")
    void deveIgnorarSeProfileNaoForProd() {
        // Arrange
        ReflectionTestUtils.setField(integracaoKommo, "profile", "dev");

        // Act
        integracaoKommo.implementacao(vendedor, cliente, conversaAgente);

        // Assert
        verifyNoInteractions(gateway);
        verifyNoInteractions(criptografiaJCAUseCase);
    }

    @Test
    @DisplayName("Deve lançar exceção se configuração CRM for nula")
    void deveLancarErroConfiguracaoNula() {
        // Arrange
        cliente.getUsuario().setConfiguracaoCrm(null);

        // Act & Assert
        assertThrows(ConfiguraCrmUsuarioNaoConfiguradaException.class,
                () -> integracaoKommo.implementacao(vendedor, cliente, conversaAgente));
    }

    @Test
    @DisplayName("Deve lançar exceção se Mapeamento for nulo")
    void deveLancarErroMapeamentoNulo() {
        // Arrange
        configCrm.setMapeamentoCampos(null);

        // Act & Assert
        assertThrows(ConfiguraCrmUsuarioNaoConfiguradaException.class,
                () -> integracaoKommo.implementacao(vendedor, cliente, conversaAgente));
    }

    @Test
    @DisplayName("Deve lançar exceção se Lead não for encontrado")
    void deveLancarErroLeadNaoEncontrado() {
        // Arrange
        conversaAgente.setStatus(StatusConversa.ATIVO);
        when(criptografiaJCAUseCase.descriptografar(any())).thenReturn("token");

        // Simula lead não encontrado
        when(gateway.consultaLeadPeloTelefone(any(), any(), any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(LeadNaoEncontradoException.class,
                () -> integracaoKommo.implementacao(vendedor, cliente, conversaAgente));
    }

    @Test
    @DisplayName("Deve retornar o CrmType correto")
    void deveRetornarTipoKommo() {
        assertEquals(CrmType.KOMMO, integracaoKommo.getCrmType());
    }
}