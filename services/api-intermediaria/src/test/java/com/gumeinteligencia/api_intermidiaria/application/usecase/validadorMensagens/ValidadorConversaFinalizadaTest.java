package com.gumeinteligencia.api_intermidiaria.application.usecase.validadorMensagens;

import com.gumeinteligencia.api_intermidiaria.application.usecase.ConversaAgenteUseCase;
import com.gumeinteligencia.api_intermidiaria.domain.ConversaAgente;
import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidadorConversaFinalizadaTest {

    @Mock
    private ConversaAgenteUseCase conversaAgenteUseCase;

    @InjectMocks
    private ValidadorConversaFinalizada validador;

    @Test
    void deveIgnorarQuandoConversaEstiverFinalizada() {
        // Prepara
        Mensagem mensagem = Mensagem.builder().telefone("45999999999").build();
        ConversaAgente conversaFinalizada = ConversaAgente.builder().finalizada(true).build();

        when(conversaAgenteUseCase.consultarPorTelefoneCliente("45999999999"))
                .thenReturn(Optional.of(conversaFinalizada));

        // Executa
        boolean resultado = validador.deveIgnorar(mensagem);

        // Verifica
        assertTrue(resultado, "Deveria ignorar pois a conversa já foi finalizada");
        verify(conversaAgenteUseCase).consultarPorTelefoneCliente("45999999999");
    }

    @Test
    void naoDeveIgnorarQuandoConversaAindaEstiverAtiva() {
        // Prepara
        Mensagem mensagem = Mensagem.builder().telefone("45999999999").build();
        ConversaAgente conversaAtiva = ConversaAgente.builder().finalizada(false).build();

        when(conversaAgenteUseCase.consultarPorTelefoneCliente("45999999999"))
                .thenReturn(Optional.of(conversaAtiva));

        // Executa
        boolean resultado = validador.deveIgnorar(mensagem);

        // Verifica
        assertFalse(resultado, "Não deveria ignorar pois a conversa ainda não foi finalizada");
    }

    @Test
    void naoDeveIgnorarQuandoConversaNaoExistir() {
        // Prepara
        Mensagem mensagem = Mensagem.builder().telefone("45999999999").build();

        when(conversaAgenteUseCase.consultarPorTelefoneCliente("45999999999"))
                .thenReturn(Optional.empty());

        // Executa
        boolean resultado = validador.deveIgnorar(mensagem);

        // Verifica
        assertFalse(resultado, "Não deveria ignorar pois é uma nova conversa (não existe no banco)");
    }
}