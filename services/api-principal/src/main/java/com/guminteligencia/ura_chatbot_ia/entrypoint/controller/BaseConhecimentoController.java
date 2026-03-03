package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.BaseConhecimentoUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.BaseConhecimentoDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.BaseConhecimentoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("base-conhecimento")
@RequiredArgsConstructor
public class BaseConhecimentoController {

    private final BaseConhecimentoUseCase baseConhecimentoUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<BaseConhecimentoDto>> cadastrar(@RequestBody BaseConhecimentoDto novoPrompt) {
        BaseConhecimentoDto resultado = BaseConhecimentoMapper.paraDto(baseConhecimentoUseCase.cadastrar(BaseConhecimentoMapper.paraDomain(novoPrompt)));
        ResponseDto<BaseConhecimentoDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(
                UriComponentsBuilder.newInstance()
                        .path("/promtps/{id}")
                        .buildAndExpand(resultado.getId())
                        .toUri()
        ).body(response);
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<ResponseDto<List<BaseConhecimentoDto>>> listar(@PathVariable("idUsuario") UUID idUsuario) {
        List<BaseConhecimentoDto> resultado = baseConhecimentoUseCase.listar(idUsuario).stream().map(BaseConhecimentoMapper::paraDto).toList();
        ResponseDto<List<BaseConhecimentoDto>> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{idPrompt}")
    public ResponseEntity<ResponseDto<BaseConhecimentoDto>> alterar(@PathVariable("idPrompt") UUID idPrompt, @RequestBody BaseConhecimentoDto novosDados) {
        BaseConhecimentoDto resultado = BaseConhecimentoMapper.paraDto(baseConhecimentoUseCase.alterar(idPrompt, BaseConhecimentoMapper.paraDomain(novosDados)));
        ResponseDto<BaseConhecimentoDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{idPrompt}")
    public ResponseEntity<Void> deletar(@PathVariable("idPrompt") UUID idPrompt) {
        baseConhecimentoUseCase.deletar(idPrompt);
        return ResponseEntity.noContent().build();
    }
}
