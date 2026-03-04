package com.gumeinteligencia.api_intermidiaria.application.usecase.validadorMensagens;

import com.gumeinteligencia.api_intermidiaria.application.usecase.UsuarioUseCase;
import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidadorSoftwareLigado implements MensagemValidator {

    private final UsuarioUseCase usuarioUseCase;

    @Override
    public boolean deveIgnorar(Mensagem mensagem) {
        boolean result = !usuarioUseCase.consultarPorTelefoneConectado(mensagem.getTelefoneConectado()).getSoftwareLigado();

        if(result) {
            log.info("Mensagem ignorada. Motivo: Software desligado");
        }

        return result;
    }
}
