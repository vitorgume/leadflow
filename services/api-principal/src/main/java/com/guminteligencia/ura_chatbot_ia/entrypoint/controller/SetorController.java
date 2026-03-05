package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.SetorUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.SetorDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.SetorMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("setores")
@RequiredArgsConstructor
public class SetorController {

    private final SetorUseCase setorUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<SetorDto>> cadastrar(@RequestBody SetorDto novoSetor) {
        SetorDto resultado = SetorMapper.paraDto(setorUseCase.cadastrar(SetorMapper.paraDomain(novoSetor)));
        ResponseDto<SetorDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(
                UriComponentsBuilder
                        .newInstance()
                        .path("/setores/{id}")
                        .buildAndExpand(resultado.getId())
                        .toUri()
        ).body(response);
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<ResponseDto<List<SetorDto>>> listar(@PathVariable("idUsuario") UUID idUsuario) {
        List<SetorDto> resultado = setorUseCase.listar(idUsuario).stream().map(SetorMapper::paraDto).toList();
        ResponseDto<List<SetorDto>> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<SetorDto>> alterar(@RequestBody SetorDto novosDados, @PathVariable("id") UUID id) {
        SetorDto resultado = SetorMapper.paraDto(setorUseCase.alterar(SetorMapper.paraDomain(novosDados), id));
        ResponseDto<SetorDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") UUID id) {
        setorUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
