package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ConfiguracaoEscolhaVendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.ConfiguracaoEscolhaVendedorDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.ConfiguracaoEscolhaVendedorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("configuracoes-escolha-vendedores")
@RequiredArgsConstructor
public class ConfiguracaoEscolhaVendedorController {

    private final ConfiguracaoEscolhaVendedorUseCase configuracaoEscolhaVendedorUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<ConfiguracaoEscolhaVendedorDto>> cadastrar(@RequestBody ConfiguracaoEscolhaVendedorDto novaConfiguracao) {
        ConfiguracaoEscolhaVendedorDto resultado = ConfiguracaoEscolhaVendedorMapper
                .paraDto(configuracaoEscolhaVendedorUseCase.cadastrar(ConfiguracaoEscolhaVendedorMapper.paraDomain(novaConfiguracao)));

        ResponseDto<ConfiguracaoEscolhaVendedorDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(UriComponentsBuilder
                .newInstance()
                .path("/configuracoes-escolha-vendedores/{id}")
                .buildAndExpand(resultado.getId())
                .toUri()
        ).body(response);
    }

    @GetMapping("/listar/{idUsuario}")
    public ResponseEntity<ResponseDto<Page<ConfiguracaoEscolhaVendedorDto>>> listar(@PathVariable("idUsuario") UUID idUsuario, @PageableDefault Pageable pageable) {
        Page<ConfiguracaoEscolhaVendedorDto> resultado = configuracaoEscolhaVendedorUseCase.listarPorUsuarioPaginado(idUsuario, pageable)
                .map(ConfiguracaoEscolhaVendedorMapper::paraDto);

        ResponseDto<Page<ConfiguracaoEscolhaVendedorDto>> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<ResponseDto<ConfiguracaoEscolhaVendedorDto>> alterar(@PathVariable("id") UUID id, @RequestBody ConfiguracaoEscolhaVendedorDto novosDados) {
        ConfiguracaoEscolhaVendedorDto resultado = ConfiguracaoEscolhaVendedorMapper
                .paraDto(configuracaoEscolhaVendedorUseCase.alterar(id, ConfiguracaoEscolhaVendedorMapper.paraDomain(novosDados)));
        ResponseDto<ConfiguracaoEscolhaVendedorDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") UUID id) {
        configuracaoEscolhaVendedorUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
