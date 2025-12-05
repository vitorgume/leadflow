package com.gumeinteligencia.api_intermidiaria.entrypoint.mapper;

import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import com.gumeinteligencia.api_intermidiaria.entrypoint.controller.dto.MensagemDto;
import com.gumeinteligencia.api_intermidiaria.entrypoint.controller.dto.TextoDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MensagemMapperTest {

    private MensagemDto mensagemDto;

    @BeforeEach
    void setUp() {
        mensagemDto = MensagemDto.builder()
                .phone("000000000000")
                .text(TextoDto.builder()
                        .message("Teste mensagem")
                        .build()
                ).build();
    }

    @Test
    void paraDomain() {
        Mensagem resultado = MensagemMapper.paraDomain(mensagemDto);

        Assertions.assertEquals(resultado.getMensagem(), mensagemDto.getText().getMessage());
        Assertions.assertEquals(resultado.getTelefone(), mensagemDto.getPhone());
    }

    @Test
    void devePreencherCamposVaziosQuandoDtoSemMidiasENemTexto() {
        MensagemDto dtoSemCampos = MensagemDto.builder()
                .phone("123456")
                .build();

        Mensagem resultado = MensagemMapper.paraDomain(dtoSemCampos);

        assertEquals("", resultado.getMensagem());
        assertEquals("", resultado.getUrlAudio());
        assertEquals("", resultado.getUrlVideo());
        assertEquals("", resultado.getUrlImagem());
    }

    @Test
    void deveMapearMidiasQuandoPresentes() {
        var mensagemDtoCompleta = MensagemDto.builder()
                .phone("9999")
                .text(TextoDto.builder().message("texto").build())
                .audio(com.gumeinteligencia.api_intermidiaria.entrypoint.controller.dto.AudioDto.builder().audioUrl("audio-url").build())
                .video(com.gumeinteligencia.api_intermidiaria.entrypoint.controller.dto.VideoDto.builder().videoUrl("video-url").build())
                .image(com.gumeinteligencia.api_intermidiaria.entrypoint.controller.dto.ImageDto.builder().imageUrl("image-url").build())
                .build();

        Mensagem resultado = MensagemMapper.paraDomain(mensagemDtoCompleta);

        assertEquals("texto", resultado.getMensagem());
        assertEquals("audio-url", resultado.getUrlAudio());
        assertEquals("video-url", resultado.getUrlVideo());
        assertEquals("image-url", resultado.getUrlImagem());
    }
}
