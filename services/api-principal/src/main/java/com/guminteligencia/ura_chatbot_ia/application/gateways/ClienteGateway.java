package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ObjetoRelatorioDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteGateway {
    Optional<Cliente> consultarPorTelefone(String telefone);

    Cliente salvar(Cliente cliente);

    Optional<Cliente> consultarPorId(UUID idCliente);

    List<ObjetoRelatorioDto> getRelatorioContato(UUID idUsuario);

    List<ObjetoRelatorioDto> getRelatorioContatoSegundaFeira(UUID idUsuario);

    Optional<Cliente> consultarPorTelefoneEUsuario(String telefone, UUID idUsuario);
}
