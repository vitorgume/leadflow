package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.CredenciasIncorretasException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.LoginGateway;
import com.guminteligencia.ura_chatbot_ia.domain.LoginResponse;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
    
    private final UsuarioUseCase usuarioUseCase;
    private final LoginGateway loginGateway;
    private final CriptografiaUseCase criptografiaUseCase;

    public LoginResponse autenticar(String email, String senha) {
        Usuario usuario = usuarioUseCase.consultarPorEmail(email);
        
        this.validaCredenciais(usuario, email, senha);
        
        String token = loginGateway.gerarToken(email);
        
        return LoginResponse.builder()
                .token(token)
                .id(usuario.getId())
                .build();
    }

    private void validaCredenciais(Usuario usuario, String email, String senha) {
        if(!usuario.getEmail().equals(email) || !criptografiaUseCase.validaSenha(senha, usuario.getSenha())) {
            throw new CredenciasIncorretasException();
        }
    }

}
