package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.ConfiguracaoEscolhaVendedorGateway;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConfiguracaoEscolhaVendedor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ConfiguracaoEscolhaVendedorMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ConfiguracaoEscolhaVendedorRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.ConfiguracaoEscolhaVendedorEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfiguracaoEscolhaVendedorDataProvider implements ConfiguracaoEscolhaVendedorGateway {

    private final ConfiguracaoEscolhaVendedorRepository repository;
    private static final String MENSAGEM_ERRO_SALVAR_CONFIGURACAO_ESCOLHA_VENDEDOR = "Erro ao salvar configuração de escolha do vendedor.";
    private static final String MENSAGEM_ERRO_LISTAR_POR_USUARIO = "Erro ao listar configurações de escolha do vendedor por usuário.";
    private static final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar configuração de escolha do vendedor por id.";

    @Override
    public ConfiguracaoEscolhaVendedor salvar(ConfiguracaoEscolhaVendedor configuracaoEscolhaVendedor) {
        ConfiguracaoEscolhaVendedorEntity configuracaoEscolhaVendedorEntity = ConfiguracaoEscolhaVendedorMapper.paraEntity(configuracaoEscolhaVendedor);

        try {
            configuracaoEscolhaVendedorEntity = repository.save(configuracaoEscolhaVendedorEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR_CONFIGURACAO_ESCOLHA_VENDEDOR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR_CONFIGURACAO_ESCOLHA_VENDEDOR, ex.getCause());
        }

        return ConfiguracaoEscolhaVendedorMapper.paraDomain(configuracaoEscolhaVendedorEntity);
    }

    @Override
    public List<ConfiguracaoEscolhaVendedor> listarPorUsuario(UUID id) {
        List<ConfiguracaoEscolhaVendedorEntity> configuracoes;

        try {
            configuracoes = repository.findByUsuario(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex.getCause());
        }

        return configuracoes.stream().map(ConfiguracaoEscolhaVendedorMapper::paraDomain).toList();
    }

    @Override
    public Page<ConfiguracaoEscolhaVendedor> listarPorUsuarioPaginado(UUID idUsuario, Pageable pageable) {
        Page<ConfiguracaoEscolhaVendedorEntity> configuracoes;

        try {
            configuracoes = repository.findByUsuario_Id(idUsuario, pageable);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex.getCause());
        }

        return configuracoes.map(ConfiguracaoEscolhaVendedorMapper::paraDomain);
    }

    @Override
    public Optional<ConfiguracaoEscolhaVendedor> consultarPorId(UUID id) {
        Optional<ConfiguracaoEscolhaVendedorEntity> configuracaoEscolhaVendedorEntity;

        try {
            configuracaoEscolhaVendedorEntity = repository.findById(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }

        return configuracaoEscolhaVendedorEntity.map(ConfiguracaoEscolhaVendedorMapper::paraDomain);
    }

    @Override
    public void deletar(UUID id) {
        this.consultarPorId(id);
        repository.deleteById(id);
    }
}
