package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CriptografiaJCADataProviderTest {

    @InjectMocks
    private CriptografiaJCADataProvider provider;

    // Chave simulada (deve ter tamanho suficiente para gerar o hash SHA-256)
    private final String SECRET_KEY = "minha-chave-secreta-super-segura";

    @BeforeEach
    void setup() {
        // Injeta o valor do @Value "${app.security.encryption-key}"
        ReflectionTestUtils.setField(provider, "key", SECRET_KEY);
    }

    @Test
    @DisplayName("Deve criptografar e descriptografar com sucesso (Round Trip)")
    void deveCriptografarEDescriptografarCorretamente() {
        // Arrange
        String textoOriginal = "Mensagem Confidencial 123";

        // Act
        String textoCriptografado = provider.criptografar(textoOriginal);
        String textoDescriptografado = provider.descriptografar(textoCriptografado);

        // Assert
        assertNotNull(textoCriptografado);
        assertNotEquals(textoOriginal, textoCriptografado); // Garante que algo mudou
        assertEquals(textoOriginal, textoDescriptografado); // Garante que voltou ao original
    }

    @Test
    @DisplayName("Segurança: Criptografar o mesmo texto duas vezes deve gerar hashes diferentes (IV aleatório)")
    void deveGerarHashesDiferentesParaMesmoInput() {
        // Arrange
        String texto = "Segredo";

        // Act
        String tentativa1 = provider.criptografar(texto);
        String tentativa2 = provider.criptografar(texto);

        // Assert
        assertNotNull(tentativa1);
        assertNotNull(tentativa2);

        // Se forem iguais, sua criptografia está vulnerável (IV fixo ou nulo)
        assertNotEquals(tentativa1, tentativa2, "O IV deve garantir aleatoriedade");

        // Mas ambos devem descriptografar para o mesmo valor
        assertEquals(texto, provider.descriptografar(tentativa1));
        assertEquals(texto, provider.descriptografar(tentativa2));
    }

    @Test
    @DisplayName("Deve retornar null se os inputs forem null")
    void deveTratarInputsNulos() {
        assertNull(provider.criptografar(null));
        assertNull(provider.descriptografar(null));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar descriptografar lixo (Base64 inválido)")
    void deveFalharAoDescriptografarBase64Invalido() {
        // Arrange
        String lixo = "IssoNaoEhBase64Valido!!!";

        // Act & Assert
        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.descriptografar(lixo));

        assertEquals("Erro ao descriptografar o valor", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar descriptografar dados adulterados (Falha na Tag GCM)")
    void deveFalharAoDescriptografarDadosAdulterados() {
        // Arrange
        String texto = "Dados Reais";
        String criptografado = provider.criptografar(texto);

        // Vamos corromper a String (trocando o último caractere válido de Base64)
        // Isso altera os bytes e falha a validação de integridade do AES/GCM
        String criptografadoCorrompido = criptografado.substring(0, criptografado.length() - 1) +
                (criptografado.endsWith("A") ? "B" : "A");

        // Act & Assert
        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.descriptografar(criptografadoCorrompido));

        assertEquals("Erro ao descriptografar o valor", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar se a chave mudar entre criptografia e descriptografia")
    void deveFalharComChaveErrada() {
        // Arrange
        String texto = "Segredo";

        // 1. Criptografa com a chave certa
        String criptografado = provider.criptografar(texto);

        // 2. Troca a chave da instância para uma chave errada
        ReflectionTestUtils.setField(provider, "key", "chave-errada-hacker");

        // Act & Assert
        // O GCM deve detectar que a chave não bate com a Tag de autenticação
        assertThrows(DataProviderException.class,
                () -> provider.descriptografar(criptografado));
    }

}