package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.LoginUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.LoginResponse;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.LoginDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.LoginResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginUseCase loginUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<LoginResponseDto>> logar(@RequestBody LoginDto loginDto) {
        // 1. Faz a autenticação normal no UseCase
        LoginResponse loginResponse = loginUseCase.autenticar(loginDto.getEmail(), loginDto.getSenha());

        // 2. Cria o Cookie HttpOnly e Seguro com o Token
        ResponseCookie jwtCookie = ResponseCookie.from("jwt_token", loginResponse.getToken())
                .httpOnly(true)       // O JavaScript (React) NÃO consegue ler isso (Proteção XSS)
                .secure(false)        // ATENÇÃO: Mude para 'true' quando for para Produção (HTTPS)
                .path("/")            // O cookie vale para todas as rotas da API
                .maxAge(4 * 60 * 60)  // 4 horas de duração (mesmo tempo do JwtUtil)
                .sameSite("Strict")   // Proteção contra ataques CSRF
                .build();

        // 3. Mapeia para DTO
        LoginResponseDto resultado = LoginMapper.paraDto(loginResponse);

        // Opcional, mas recomendado: Limpe o token do DTO para ele não ir no corpo do JSON
        resultado.setToken(null);

        ResponseDto<LoginResponseDto> response = new ResponseDto<>(resultado);

        // 4. Retorna a resposta com o Header "Set-Cookie"
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);
    }
}