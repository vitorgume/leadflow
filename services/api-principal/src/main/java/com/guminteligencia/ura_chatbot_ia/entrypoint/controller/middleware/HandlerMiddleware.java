package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.middleware;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.*;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class HandlerMiddleware {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> exceptionHandler(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(CredenciasIncorretasException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerCredenciasIncorretasException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDto.comErro(erroDto));
    }


    //Cliente

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerClienteNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    //Contexto

    @ExceptionHandler(ContextoNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerContextoNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    //Conversa

    @ExceptionHandler(ConversaAgenteNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerConversaAgenteNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(EscolhaNaoIdentificadoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerEscolhaNaoIdentificadoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(ProcessoContextoExistenteNaoIdentificadoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerProcessoContextoExistenteNaoIdentificadoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    //Vendedor

    @ExceptionHandler(VendedorComMesmoTelefoneException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerVendedorComMesmoTelefoneException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(VendedorNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerVendedorNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(VendedorNaoEscolhidoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerVendedorNaoEscolhidoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    //Outros

    @ExceptionHandler(DataProviderException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerDataProviderException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(OutroContatoNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerOutroContatoNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(ChatNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerChatNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(CondicaoLogicaNaoIdentificadoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerCondicaoLogicaNaoIdentificadoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(CondicaoNaoEncontradaException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerCondicaoNaoEncontradaException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(ConfiguracaoEscolhaVendedorNaoEncontrada.class)
    public ResponseEntity<ResponseDto> exceptionHandlerConfiguracaoEscolhaVendedorNaoEncontrada(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(ConfiguraCrmUsuarioNaoConfiguradaException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerConfiguraCrmUsuarioNaoConfiguradaException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(IntegracaoExistenteNaoIdentificada.class)
    public ResponseEntity<ResponseDto> exceptionHandlerIntegracaoExistenteNaoIdentificada(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(LeadNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerLeadNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(MidiaClienteNaoEncontradaException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerMidiaClienteNaoEncontradaException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(NenhumVendedorReferenciadoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerNenhumVendedorReferenciadoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(OutroContatoComMesmoTelefoneJaCadastradoExcetion.class)
    public ResponseEntity<ResponseDto> exceptionHandlerOutroContatoComMesmoTelefoneJaCadastradoExcetion(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(OutroContatoTipoGerenciaJaCadastradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerOutroContatoTipoGerenciaJaCadastradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(UsuarioExistenteException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerUsuarioExistenteException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(UsuarioNaoEncotradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerUsuarioNaoEncotradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    // Base Conhecimento

    @ExceptionHandler(BaseConhecimentoNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerBaseConhecimentoNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(LimiteDeUmBaseConhecimentoJaAtingidoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerLimiteDeUmBaseConhecimentoJaAtingidoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    // Prompt

    @ExceptionHandler(PromptNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerPromptNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(LimiteDeUmPromptJaAtingidoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerLimiteDeUmPromptJaAtingidoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    private List<String> mensagens(String mensagem) {
        return mensagem != null ? List.of(mensagem) : List.of("Erro interno inesperado.");
    }
}
