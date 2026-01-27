package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ConfiguracaoEscolhaVendedorNaoEncontrada;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ConfiguracaoEscolhaVendedorGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.UsuarioUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConfiguracaoEscolhaVendedor;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfiguracaoEscolhaVendedorUseCase {

    private final ConfiguracaoEscolhaVendedorGateway gateway;
    private final UsuarioUseCase usuarioUseCase;
    private final VendedorUseCase vendedorUseCase;
    private final CondicaoUseCase condicaoUseCase;

    public ConfiguracaoEscolhaVendedor cadastrar(ConfiguracaoEscolhaVendedor configuracaoEscolhaVendedor) {
        log.info("Cadastrnado configuracao do vendedor. Configuracao: {}", configuracaoEscolhaVendedor);

        Usuario usuario = usuarioUseCase.consultarPorId(configuracaoEscolhaVendedor.getUsuario().getId());

        List<Vendedor> vendedores = configuracaoEscolhaVendedor.getVendedores()
                .stream()
                .map(vendedor -> vendedorUseCase.consultarPorId(vendedor.getId()))
                .toList();

        List<Condicao> condicoes = configuracaoEscolhaVendedor.getCondicoes();

        configuracaoEscolhaVendedor.setUsuario(usuario);
        configuracaoEscolhaVendedor.setVendedores(vendedores);
        configuracaoEscolhaVendedor.setCondicoes(condicoes);

        configuracaoEscolhaVendedor = gateway.salvar(configuracaoEscolhaVendedor);

        log.info("Configuração salva com sucesso. Configuração: {}", configuracaoEscolhaVendedor);

        return configuracaoEscolhaVendedor;
    }

    public List<ConfiguracaoEscolhaVendedor> listarPorUsuario(UUID id) {
        return gateway.listarPorUsuario(id);
    }

    public Page<ConfiguracaoEscolhaVendedor> listarPorUsuarioPaginado(UUID idUsuario, Pageable pageable) {
        return gateway.listarPorUsuarioPaginado(idUsuario, pageable);
    }


    public ConfiguracaoEscolhaVendedor alterar(UUID id, ConfiguracaoEscolhaVendedor configuracaoEscolhaVendedor) {
        ConfiguracaoEscolhaVendedor configuracaoExistente = this.consultarPorId(id);

        if(configuracaoEscolhaVendedor.getVendedores() == null || configuracaoEscolhaVendedor.getVendedores().isEmpty()) {
            configuracaoEscolhaVendedor.setVendedores(configuracaoExistente.getVendedores());
        } else {
            List<Vendedor> vendedores = configuracaoEscolhaVendedor.getVendedores()
                    .stream()
                    .map(vendedor -> vendedorUseCase.consultarPorId(vendedor.getId()))
                    .toList();

            configuracaoEscolhaVendedor.setVendedores(vendedores);
        }

        if(configuracaoEscolhaVendedor.getCondicoes() == null || configuracaoEscolhaVendedor.getCondicoes().isEmpty()) {
            configuracaoEscolhaVendedor.setCondicoes(configuracaoExistente.getCondicoes());
        } else {
            List<Condicao> condicoes = configuracaoEscolhaVendedor.getCondicoes()
                    .stream()
                    .map(condicao -> condicaoUseCase.consultarPorId(condicao.getId()))
                    .toList();

            configuracaoEscolhaVendedor.setCondicoes(condicoes);
        }

        configuracaoExistente.setDados(configuracaoEscolhaVendedor);

        return gateway.salvar(configuracaoExistente);
    }

    private ConfiguracaoEscolhaVendedor consultarPorId(UUID id) {
        Optional<ConfiguracaoEscolhaVendedor> configuracaoEscolhaVendedor = gateway.consultarPorId(id);

        if(configuracaoEscolhaVendedor.isEmpty()) {
            throw new ConfiguracaoEscolhaVendedorNaoEncontrada();
        }

        return configuracaoEscolhaVendedor.get();
    }

    public void deletar(UUID id) {
        ConfiguracaoEscolhaVendedor configuracao = this.consultarPorId(id);

        configuracao.getCondicoes().forEach(condicao -> condicaoUseCase.deletar(condicao.getId()));

        gateway.deletar(id);
    }
}
