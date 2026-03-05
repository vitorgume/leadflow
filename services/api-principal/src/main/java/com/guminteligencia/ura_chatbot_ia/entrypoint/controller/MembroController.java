package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.MembroUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.MembroDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.MembroMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("membros")
public class MembroController {

    private final MembroUseCase membroUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<MembroDto>> cadastrar(@RequestBody MembroDto novoMembro) {
        MembroDto resultado = MembroMapper.paraDto(membroUseCase.cadastrar(MembroMapper.paraDomain(novoMembro)));
        ResponseDto<MembroDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(
                UriComponentsBuilder
                        .newInstance()
                        .path("/membros/{id}")
                        .buildAndExpand(resultado.getId())
                        .toUri()
        ).body(response);
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<ResponseDto<List<MembroDto>>> listar(@PathVariable("idUsuario") UUID idUsuario) {
        List<MembroDto> resultado = membroUseCase.listar(idUsuario).stream().map(MembroMapper::paraDto).toList();
        ResponseDto<List<MembroDto>> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<MembroDto>> alterar(@RequestBody MembroDto novosDados, @PathVariable("id") UUID id) {
        MembroDto resultado = MembroMapper.paraDto(membroUseCase.alterar(MembroMapper.paraDomain(novosDados), id));
        ResponseDto<MembroDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") UUID id) {
        membroUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
