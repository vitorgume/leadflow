package com.gumeinteligencia.api_intermidiaria.infrastructure.dataprovider;

import com.gumeinteligencia.api_intermidiaria.application.gateways.ConversaAgenteGateway;
import com.gumeinteligencia.api_intermidiaria.domain.ConversaAgente;
import com.gumeinteligencia.api_intermidiaria.infrastructure.exceptions.DataProviderException;
import com.gumeinteligencia.api_intermidiaria.infrastructure.mapper.ConversaAgenteMapper;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.ConversaAgenteRepository;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ConversaAgenteEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConversaAgenteDataProvider implements ConversaAgenteGateway {

    private final ConversaAgenteRepository repository;
    private static final String MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE_CLIENTE = "Erro ao consultar conversa pelo telefone do cliente.";

    @Override
    public Optional<ConversaAgente> consultarPorTelefoneCliente(String telefone) {
        Optional<ConversaAgenteEntity> conversaAgente;

        try {
            conversaAgente = repository.findByCliente_Telefone(telefone);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE_CLIENTE, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE_CLIENTE, ex.getCause());
        }

        return conversaAgente.map(ConversaAgenteMapper::paraDomain);
    }
}
