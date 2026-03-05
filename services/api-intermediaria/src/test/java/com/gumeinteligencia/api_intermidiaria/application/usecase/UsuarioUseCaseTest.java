package com.gumeinteligencia.api_intermidiaria.application.usecase;

import com.gumeinteligencia.api_intermidiaria.application.exceptions.UsuarioNaoEncontradoException;
import com.gumeinteligencia.api_intermidiaria.application.gateways.UsuarioGateway;
import com.gumeinteligencia.api_intermidiaria.domain.Usuario;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.UsuarioEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioGateway gateway;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuario;
    private final String TELEFONE_CONECTADO = "554463215478";

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Usuario teste")
                .telefone("554498748356")
                .email("userteste@gmail.com")
                .telefoneConectado(TELEFONE_CONECTADO)
                .softwareLigado(true)
                .build();
    }

    @Test
    void deveConsultarPorTelefoneConectadoComSucesso() {
        Mockito.when(gateway.consultarPorTelefoneConectado(TELEFONE_CONECTADO)).thenReturn(Optional.of(usuario));

        Usuario result = usuarioUseCase.consultarPorTelefoneConectado(TELEFONE_CONECTADO);

        Assertions.assertEquals(result.getId(), usuario.getId());

        Mockito.verify(gateway).consultarPorTelefoneConectado(TELEFONE_CONECTADO);
    }

    @Test
    void deveLancarExceptionQuandoNaoRetornaNenhumUsuario() {
        Mockito.when(gateway.consultarPorTelefoneConectado(TELEFONE_CONECTADO)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsuarioNaoEncontradoException.class, () -> usuarioUseCase.consultarPorTelefoneConectado(TELEFONE_CONECTADO));
    }
}