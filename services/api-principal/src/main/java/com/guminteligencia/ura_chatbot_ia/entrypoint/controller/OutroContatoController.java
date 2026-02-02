package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.OutroContatoUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.OutroContatoDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.OutroContatoMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("outros-contatos")
@RequiredArgsConstructor
public class OutroContatoController {

    private final OutroContatoUseCase useCase;

    @PostMapping
    public ResponseEntity<ResponseDto<OutroContatoDto>> cadastrar(@RequestBody OutroContatoDto novoOutroContato) {
        OutroContatoDto resultado = OutroContatoMapper.paraDto(useCase.cadastrar(OutroContatoMapper.paraDomain(novoOutroContato)));
        ResponseDto<OutroContatoDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(UriComponentsBuilder
                .newInstance()
                .path("/outros-contatos/{id}")
                .buildAndExpand(resultado.getId())
                .toUri()
        ).body(response);
    }

    @GetMapping("/listar/{idUsuario}")
    public ResponseEntity<ResponseDto<Page<OutroContatoDto>>> listar(@PageableDefault Pageable pageable, @PathVariable("idUsuario") UUID idUsuario) {
        Page<OutroContatoDto> resultado = useCase.listar(pageable, idUsuario).map(OutroContatoMapper::paraDto);
        ResponseDto<Page<OutroContatoDto>> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{idOutroContato}")
    public ResponseEntity<ResponseDto<OutroContatoDto>> alterar(
            @PathVariable("idOutroContato") Long idOutroContato, @RequestBody OutroContatoDto novosDados
    ) {
        OutroContatoDto resultado = OutroContatoMapper.paraDto(useCase.alterar(idOutroContato, OutroContatoMapper.paraDomain(novosDados)));
        ResponseDto<OutroContatoDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long id) {
        useCase.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
