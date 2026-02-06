package com.gumeinteligencia.api_intermidiaria.application.usecase.validadorMensagens;

import com.gumeinteligencia.api_intermidiaria.application.usecase.OutroContatoUseCase;
<<<<<<< HEAD
import com.gumeinteligencia.api_intermidiaria.domain.outroContato.OutroContato;
=======
>>>>>>> main
import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidadorTelefoneOutroContato implements MensagemValidator {

    private final OutroContatoUseCase outroContatoUseCase;

    @Override
    public boolean deveIgnorar(Mensagem mensagem) {
        return outroContatoUseCase.consultarPorTelefone(mensagem.getTelefone()).isPresent();
    }
}
