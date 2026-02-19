package com.gumeinteligencia.api_intermidiaria.application.usecase.validadorMensagens;

import com.gumeinteligencia.api_intermidiaria.application.usecase.OutroContatoUseCase;
import com.gumeinteligencia.api_intermidiaria.domain.outroContato.OutroContato;
import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidadorTelefoneOutroContato implements MensagemValidator {

    private final OutroContatoUseCase outroContatoUseCase;

    @Override
    public boolean deveIgnorar(Mensagem mensagem) {
        boolean result = outroContatoUseCase.listar().stream().map(OutroContato::getTelefone).toList().contains(mensagem.getTelefone());

        if(result) {
            log.info("Mensagem ignorada. Motivo: Telefone de um Outro Contato");
        }

        return result;
    }
}
