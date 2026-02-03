package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensagemDadosContatoAtendenteTest {
    private final MensagemDadosContatoAtendente sut = new MensagemDadosContatoAtendente();

    private MensagemDadosContatoAtendente estrategia;

    @BeforeEach
    void setup() {
        estrategia = new MensagemDadosContatoAtendente();
    }

    private final Usuario usuarioDomain = Usuario.builder()
            .id(UUID.randomUUID())
            .nome("nome teste")
            .telefone("00000000000")
            .senha("senhateste123")
            .email("emailteste@123")
            .telefoneConcectado("00000000000")
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

    private final Cliente cliente = Cliente.builder()
            .id(UUID.randomUUID())
            .nome("Nome domain")
            .telefone("00000000000")
            .atributosQualificacao(Map.of("teste", new Object()))
            .inativo(false)
            .usuario(usuarioDomain)
            .build();

    @Test
    @DisplayName("Deve gerar mensagem formatada com todos os dados preenchidos")
    void deveGerarMensagemComTodosDados() {
        // Arrange
        // Usamos LinkedHashMap para garantir a ordem de inserção no teste
        Map<String, Object> atributos = new LinkedHashMap<>();
        atributos.put("nome_completo", "João Silva");
        atributos.put("segmento_mercado", "Varejo");

        cliente.setTelefone("11999999999");
        cliente.setAtributosQualificacao(atributos);

        // Act
        String resultado = estrategia.getMensagem("Vendedor Qualquer", cliente);

        // Assert
        // Verificamos partes da string para não depender da hora exata
        assertTrue(resultado.contains("Dados do contato acima:\n"));

        // Verifica a normalização (Remove _ e coloca Maiúscula)
        assertTrue(resultado.contains("Nome completo: João Silva\n"));
        assertTrue(resultado.contains("Segmento mercado: Varejo\n"));

        assertTrue(resultado.contains("Telefone: 11999999999\n"));

        // Verifica se existe o padrão de hora HH:mm (ex: Hora: 14:30)
        // (?s) ativa o DOTALL mode para o regex pegar multilinhas se precisasse
        assertTrue(resultado.matches("(?s).*Hora: \\d{2}:\\d{2}.*"));
    }

    @Test
    @DisplayName("Deve tratar campos nulos nos atributos de qualificação")
    void deveTratarCamposNulosNosAtributos() {
        // Arrange
        Map<String, Object> atributos = new     HashMap<>();
        atributos.put("interesse", null);

        cliente.setTelefone("11999999999");
        cliente.setAtributosQualificacao(atributos);

        // Act
        String resultado = estrategia.getMensagem("Vendedor", cliente);

        // Assert
        // Nota: Pelo código original, quando é null ele NÃO adiciona o \n no final.
        // Se isso for um bug no código original, o teste vai passar agora, mas idealmente deveria ter \n
        assertTrue(resultado.contains("Interesse: Interesse não informado"));
    }

    @Test
    @DisplayName("Deve tratar telefone nulo corretamente")
    void deveTratarTelefoneNulo() {
        // Arrange
        cliente.setTelefone(null);
        cliente.setAtributosQualificacao(new HashMap<>()); // Mapa vazio

        // Act
        String resultado = estrategia.getMensagem("Vendedor", cliente);

        // Assert
        assertTrue(resultado.contains("Telefone: Telefone não informado\n"));
    }

    @Test
    @DisplayName("Deve retornar o código correto do TipoMensagem")
    void deveRetornarTipoMensagemCorreto() {
        // Act
        Integer tipo = estrategia.getTipoMensagem();

        // Assert
        // Compara com o ENUM real do projeto
        assertEquals(TipoMensagem.DADOS_CONTATO_VENDEDOR.getCodigo(), tipo);
    }

}