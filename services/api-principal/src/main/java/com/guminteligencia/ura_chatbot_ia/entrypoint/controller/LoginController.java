package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.LoginUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.LoginResponse;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.LoginDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.LoginResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.profiles.active}")
    private String profile;

    @PostMapping
    public ResponseEntity<ResponseDto<LoginResponseDto>> logar(@RequestBody LoginDto loginDto) {
        LoginResponse loginResponse = loginUseCase.autenticar(loginDto.getEmail(), loginDto.getSenha());

        ResponseCookie jwtCookie = ResponseCookie.from("jwt_token", loginResponse.getToken())
                .httpOnly(true)
                .secure(!profile.equals("dev"))
                .path("/")
                .maxAge(4 * 60 * 60)
                .sameSite("None")
                .build();

        LoginResponseDto resultado = LoginMapper.paraDto(loginResponse);

        resultado.setToken(null);

        ResponseDto<LoginResponseDto> response = new ResponseDto<>(resultado);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);
    }
}