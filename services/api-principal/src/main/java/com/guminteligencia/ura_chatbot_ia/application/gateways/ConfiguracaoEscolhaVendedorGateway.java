package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConfiguracaoEscolhaVendedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConfiguracaoEscolhaVendedorGateway {
    ConfiguracaoEscolhaVendedor salvar(ConfiguracaoEscolhaVendedor configuracaoEscolhaVendedor);

    List<ConfiguracaoEscolhaVendedor> listarPorUsuario(UUID id);

    Page<ConfiguracaoEscolhaVendedor> listarPorUsuarioPaginado(UUID idUsuario, Pageable pageable);

    Optional<ConfiguracaoEscolhaVendedor> consultarPorId(UUID id);

    void deletar(UUID id);
}
