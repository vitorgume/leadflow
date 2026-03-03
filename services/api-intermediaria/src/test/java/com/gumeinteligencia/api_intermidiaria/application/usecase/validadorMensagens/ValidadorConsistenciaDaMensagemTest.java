package com.gumeinteligencia.api_intermidiaria.application.usecase.validadorMensagens;

import static org.junit.jupiter.api.Assertions.*;

import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorConsistenciaDaMensagemTest {

    private ValidadorConsistenciaDaMensagem validador;

    @BeforeEach
    void setUp() {
        // Como não tem injeção de dependência, instanciamos direto
        validador = new ValidadorConsistenciaDaMensagem();
    }

    @Test
    void deveIgnorarQuandoTodosOsCamposForemNulos() {
        Mensagem mensagem = Mensagem.builder()
                .mensagem(null)
                .urlImagem(null)
                .urlAudio(null)
                .urlVideo(null)
                .build();

        boolean resultado = validador.deveIgnorar(mensagem);
        assertTrue(resultado, "Deveria ignorar mensagem com todos os campos nulos");
    }

    @Test
    void deveIgnorarQuandoTodosOsCamposForemVazios() {
        Mensagem mensagem = Mensagem.builder()
                .mensagem("   ")
                .urlImagem("")
                .urlAudio(" ")
                .urlVideo("")
                .build();

        boolean resultado = validador.deveIgnorar(mensagem);
        assertTrue(resultado, "Deveria ignorar mensagem com todos os campos em branco");
    }

    @Test
    void naoDeveIgnorarQuandoTiverMensagemDeTexto() {
        Mensagem mensagem = Mensagem.builder()
                .mensagem("Olá, gostaria de um orçamento")
                .urlImagem(null)
                .urlAudio("")
                .build();

        boolean resultado = validador.deveIgnorar(mensagem);
        assertFalse(resultado, "Não deveria ignorar pois possui texto");
    }

    @Test
    void naoDeveIgnorarQuandoTiverImagem() {
        Mensagem mensagem = Mensagem.builder()
                .urlImagem("https://link-da-imagem.com/img.jpg")
                .build();

        boolean resultado = validador.deveIgnorar(mensagem);
        assertFalse(resultado, "Não deveria ignorar pois possui imagem");
    }

    @Test
    void naoDeveIgnorarQuandoTiverAudio() {
        Mensagem mensagem = Mensagem.builder()
                .urlAudio("https://link-do-audio.com/audio.ogg")
                .build();

        boolean resultado = validador.deveIgnorar(mensagem);
        assertFalse(resultado, "Não deveria ignorar pois possui áudio");
    }

    @Test
    void naoDeveIgnorarQuandoTiverVideo() {
        Mensagem mensagem = Mensagem.builder()
                .urlVideo("https://link-do-video.com/video.mp4")
                .build();

        boolean resultado = validador.deveIgnorar(mensagem);
        assertFalse(resultado, "Não deveria ignorar pois possui vídeo");
    }
}