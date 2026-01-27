package com.guminteligencia.ura_chatbot_ia.entrypoint;

import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.CondicaoUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.CondicaoDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.CondicaoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("condicoes")
@RequiredArgsConstructor
public class CondicaoController {

    private final CondicaoUseCase condicaoUseCase;

    @PutMapping("id")
    public ResponseEntity<ResponseDto<CondicaoDto>> alterar(@PathVariable("id") UUID id, @RequestBody CondicaoDto novosDados) {
        CondicaoDto resultado = CondicaoMapper.paraDto(condicaoUseCase.alterar(id, CondicaoMapper.paraDomain(novosDados)));
        ResponseDto<CondicaoDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("id")
    public ResponseEntity<Void> deletar(@PathVariable("id") UUID id) {
        condicaoUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
