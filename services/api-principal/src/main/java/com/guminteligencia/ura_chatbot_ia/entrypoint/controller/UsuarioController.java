package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.UsuarioUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;

    public ResponseEntity<ResponseDto<UsuarioDto>> cadastrar(@RequestBody UsuarioDto novoUsuario) {
        UsuarioDto resultado = UsuarioMapper.paraDto(usuarioUseCase.cadastrar(UsuarioMapper.paraDomain(novoUsuario)));
        ResponseDto<UsuarioDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(UriComponentsBuilder
                .newInstance()
                .path("/usuarios/{id}")
                .buildAndExpand(resultado.getId())
                .toUri()
        ).body(response);
    }

    public ResponseEntity<ResponseDto<UsuarioDto>> consultarPorId(@PathVariable("id") UUID id) {
        UsuarioDto resultado = UsuarioMapper.paraDto(usuarioUseCase.consultarPorId(id));
        ResponseDto<UsuarioDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Void> deletar(@PathVariable("id") UUID id) {
        usuarioUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
