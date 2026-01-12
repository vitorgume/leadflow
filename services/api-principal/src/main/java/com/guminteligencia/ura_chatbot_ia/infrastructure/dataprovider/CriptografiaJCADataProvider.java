package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.CriptografiaJCAGateway;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@Slf4j
public class CriptografiaJCADataProvider implements CriptografiaJCAGateway {

    @Value("${app.security.encryption-key}")
    private String key;

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12; // 96 bits, recomendado para GCM.
    private static final int TAG_LENGTH_BITS = 128; // Tag de autenticação do GCM.
    private static final String MENSAGEM_ERRO_CRIPTOGRAFAR = "Erro ao criptografar o valor";
    private static final String MENSAGEM_ERRO_DESCRIPTORAFAR = "Erro ao descriptografar o valor";

    @Override
    public String criptografar(String valor) {
        if (valor == null) return null;
        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            new SecureRandom().nextBytes(iv); // IV sempre aleatório!

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

            byte[] cipherText = cipher.doFinal(valor.getBytes(StandardCharsets.UTF_8));

            // Prepend o IV ao ciphertext para uso na descriptografia.
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            log.error(MENSAGEM_ERRO_CRIPTOGRAFAR, e.getCause());
            throw new DataProviderException(MENSAGEM_ERRO_CRIPTOGRAFAR, e);
        }
    }

    @Override
    public String descriptografar(String valorCriptografado) {
        if (valorCriptografado == null) return null;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(valorCriptografado);
            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedBytes);

            byte[] iv = new byte[IV_LENGTH_BYTES];
            byteBuffer.get(iv);

            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(MENSAGEM_ERRO_DESCRIPTORAFAR, e.getCause());
            throw new DataProviderException(MENSAGEM_ERRO_DESCRIPTORAFAR, e);
        }
    }
}
