package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.RelatorioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioUseCase relatorioUseCase;

    @RequestMapping("/gerar")
    public ResponseEntity<Void> gerarRelatorio() {
        relatorioUseCase.enviarRelatorioDiarioVendedores();
        return ResponseEntity.ok().build();
    }

}
