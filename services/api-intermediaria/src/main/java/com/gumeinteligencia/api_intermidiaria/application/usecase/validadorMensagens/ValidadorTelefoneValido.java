package com.gumeinteligencia.api_intermidiaria.application.usecase.validadorMensagens;

import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class ValidadorTelefoneValido implements MensagemValidator {

    @Override
    public boolean deveIgnorar(Mensagem mensagem) {

        boolean result = !PADRAO_FLEX.matcher(mensagem.getTelefone()).matches();

        if(result) {
            log.info("Mensagem ignorada. Motivo: Telefone inválido");
        }

        return result;
    }

    private static final Pattern PADRAO_FLEX = Pattern.compile(
            "^(?:\\+?55\\s?)?\\(?\\d{2}\\)?\\s?(?:9\\d{4}|\\d{4})-?\\d{4}$"
    );
}
