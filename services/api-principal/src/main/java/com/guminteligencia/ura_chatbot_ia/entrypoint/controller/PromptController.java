package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.PromptUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.PromptDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.PromptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("prompts")
@RequiredArgsConstructor
public class PromptController {

    private final PromptUseCase promptUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<PromptDto>> cadastrar(@RequestBody PromptDto novoPrompt) {
        PromptDto resultado = PromptMapper.paraDto(promptUseCase.cadastrar(PromptMapper.paraDomain(novoPrompt)));
        ResponseDto<PromptDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(
                UriComponentsBuilder.newInstance()
                        .path("/promtps/{id}")
                        .buildAndExpand(resultado.getId())
                        .toUri()
        ).body(response);
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<ResponseDto<List<PromptDto>>> listar(@PathVariable("idUsuario") UUID idUsuario) {
        List<PromptDto> resultado = promptUseCase.listar(idUsuario).stream().map(PromptMapper::paraDto).toList();
        ResponseDto<List<PromptDto>> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{idPrompt}")
    public ResponseEntity<ResponseDto<PromptDto>> alterar(@PathVariable("idPrompt") UUID idPrompt, @RequestBody PromptDto novosDados) {
        PromptDto resultado = PromptMapper.paraDto(promptUseCase.alterar(idPrompt, PromptMapper.paraDomain(novosDados)));
        ResponseDto<PromptDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{idPrompt}")
    public ResponseEntity<Void> deletar(@PathVariable("idPrompt") UUID idPrompt) {
        promptUseCase.deletar(idPrompt);
        return ResponseEntity.noContent().build();
    }
}
