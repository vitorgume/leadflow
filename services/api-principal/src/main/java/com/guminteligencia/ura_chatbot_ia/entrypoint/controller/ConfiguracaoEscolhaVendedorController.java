package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.ConfiguracaoEscolhaVendedorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConfiguracaoEscolhaVendedorController {

    @PostMapping
    public ResponseEntity<ResponseDto<ConfiguracaoEscolhaVendedorDto>> cadastrar(@RequestBody ConfiguracaoEscolhaVendedorDto novaConfiguracao) {
        
    }

}
