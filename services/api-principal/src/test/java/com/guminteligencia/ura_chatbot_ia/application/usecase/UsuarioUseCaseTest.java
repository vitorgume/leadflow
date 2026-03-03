package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.UsuarioExistenteException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.UsuarioNaoEncotradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.UsuarioGateway;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioGateway gateway;

    @Mock
    private CriptografiaUseCase criptografiaUseCase;

    @Mock
    private CriptografiaJCAUseCase criptografiaJCAUseCase;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuarioTeste;

    @BeforeEach
    void setUp() {
        usuarioTeste = new Usuario();
        usuarioTeste.setEmail("test@example.com");
        usuarioTeste.setSenha("password");
        usuarioTeste.setConfiguracaoCrm(new ConfiguracaoCrm(null, null, null, null, null, null, "accessToken", null));
        usuarioTeste.setWhatsappToken("whatsappToken");
        usuarioTeste.setWhatsappIdInstance("whatsappIdInstance");
        usuarioTeste.setAgenteApiKey("agenteApiKey");
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {
        Mockito.when(gateway.consultarPorEmail(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(criptografiaUseCase.criptografar(Mockito.anyString())).thenReturn("encryptedPassword");
        Mockito.when(criptografiaJCAUseCase.criptografar(Mockito.anyString())).thenReturn("encryptedValue");
        Mockito.when(gateway.salvar(Mockito.any(Usuario.class))).thenReturn(usuarioTeste);

        Usuario resultado = usuarioUseCase.cadastrar(usuarioTeste);

        Assertions.assertNotNull(resultado);
        Mockito.verify(gateway, Mockito.times(1)).salvar(usuarioTeste);
    }

    @Test
    void deveLancarExceptionAoCadastrarUsuarioExistente() {
        Mockito.when(gateway.consultarPorEmail(Mockito.anyString())).thenReturn(Optional.of(usuarioTeste));

        Assertions.assertThrows(UsuarioExistenteException.class, () -> usuarioUseCase.cadastrar(usuarioTeste));
    }

    @Test
    void deveConsultarUsuarioPorIdComSucesso() {
        UUID id = UUID.randomUUID();
        usuarioTeste.setId(id);
        Mockito.when(gateway.consultarPorId(id)).thenReturn(Optional.of(usuarioTeste));

        Usuario resultado = usuarioUseCase.consultarPorId(id);

        Assertions.assertEquals(usuarioTeste, resultado);
    }

    @Test
    void deveLancarExceptionAoConsultarUsuarioPorIdNaoExistente() {
        UUID id = UUID.randomUUID();
        Mockito.when(gateway.consultarPorId(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsuarioNaoEncotradoException.class, () -> usuarioUseCase.consultarPorId(id));
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        UUID id = UUID.randomUUID();
        usuarioTeste.setId(id);
        Mockito.when(gateway.consultarPorId(id)).thenReturn(Optional.of(usuarioTeste));

        usuarioUseCase.deletar(id);

        Mockito.verify(gateway, Mockito.times(1)).deletar(id);
    }

    @Test
    void deveLancarExceptionAoDeletarUsuarioNaoExistente() {
        UUID id = UUID.randomUUID();
        Mockito.when(gateway.consultarPorId(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsuarioNaoEncotradoException.class, () -> usuarioUseCase.deletar(id));
    }

    @Test
    void deveConsultarUsuarioPorEmailComSucesso() {
        String email = "test@example.com";
        Mockito.when(gateway.consultarPorEmail(email)).thenReturn(Optional.of(usuarioTeste));

        Usuario resultado = usuarioUseCase.consultarPorEmail(email);

        Assertions.assertEquals(usuarioTeste, resultado);
    }

    @Test
    void deveLancarExceptionAoConsultarUsuarioPorEmailNaoExistente() {
        String email = "test@example.com";
        Mockito.when(gateway.consultarPorEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsuarioNaoEncotradoException.class, () -> usuarioUseCase.consultarPorEmail(email));
    }

    @Test
    void deveConsultarUsuarioPorTelefoneConectadoComSucesso() {
        String telefone = "123456789";
        Mockito.when(gateway.consultarPorTelefoneConectado(telefone)).thenReturn(Optional.of(usuarioTeste));

        Usuario resultado = usuarioUseCase.consultarPorTelefoneConectado(telefone);

        Assertions.assertEquals(usuarioTeste, resultado);
    }

    @Test
    void deveLancarExceptionAoConsultarUsuarioPorTelefoneConectadoNaoExistente() {
        String telefone = "123456789";
        Mockito.when(gateway.consultarPorTelefoneConectado(telefone)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsuarioNaoEncotradoException.class, () -> usuarioUseCase.consultarPorTelefoneConectado(telefone));
    }

    @Test
    void deveListarUsuarios() {
        List<Usuario> usuarios = Collections.singletonList(usuarioTeste);
        Mockito.when(gateway.listar()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioUseCase.listar();

        Assertions.assertEquals(usuarios, resultado);
    }

    @Test
    void deveAlterarUsuarioComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Usamos spy para monitorar se o método setDados será chamado
        Usuario usuarioExistente = Mockito.spy(new Usuario());
        usuarioExistente.setId(id);

        Usuario novosDados = new Usuario();
        novosDados.setConfiguracaoCrm(new ConfiguracaoCrm(null, null, null, null, null, null, "novoTokenCrm", null));
        novosDados.setWhatsappToken("novoTokenWpp");
        novosDados.setWhatsappIdInstance("novaInstance");
        novosDados.setAgenteApiKey("novaApiKey");

        Mockito.when(gateway.consultarPorId(id)).thenReturn(Optional.of(usuarioExistente));

        // Mockando os retornos da criptografia
        Mockito.when(criptografiaJCAUseCase.criptografar("novoTokenCrm")).thenReturn("crmCriptografado");
        Mockito.when(criptografiaJCAUseCase.criptografar("novoTokenWpp")).thenReturn("wppCriptografado");
        Mockito.when(criptografiaJCAUseCase.criptografar("novaInstance")).thenReturn("instanceCriptografada");
        Mockito.when(criptografiaJCAUseCase.criptografar("novaApiKey")).thenReturn("apiCriptografada");

        Mockito.when(gateway.salvar(usuarioExistente)).thenReturn(usuarioExistente);

        // Act
        Usuario resultado = usuarioUseCase.alterar(id, novosDados);

        // Assert
        Assertions.assertNotNull(resultado);

        // Garante que os valores foram sobrescritos pela versão criptografada ANTES do setDados
        Assertions.assertEquals("crmCriptografado", novosDados.getConfiguracaoCrm().getAcessToken());
        Assertions.assertEquals("wppCriptografado", novosDados.getWhatsappToken());
        Assertions.assertEquals("instanceCriptografada", novosDados.getWhatsappIdInstance());
        Assertions.assertEquals("apiCriptografada", novosDados.getAgenteApiKey());

        // Garante que injetou os novos dados na entidade gerenciada e salvou
        Mockito.verify(usuarioExistente).setDados(novosDados);
        Mockito.verify(gateway).salvar(usuarioExistente);
    }

    @Test
    void deveLancarExceptionAoAlterarUsuarioNaoExistente() {
        // Arrange
        UUID id = UUID.randomUUID();
        Usuario novosDados = new Usuario();

        Mockito.when(gateway.consultarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(UsuarioNaoEncotradoException.class, () -> usuarioUseCase.alterar(id, novosDados));

        // Garante que não tentou salvar nem criptografar nada
        Mockito.verify(criptografiaJCAUseCase, Mockito.never()).criptografar(Mockito.anyString());
        Mockito.verify(gateway, Mockito.never()).salvar(Mockito.any());
    }
}
