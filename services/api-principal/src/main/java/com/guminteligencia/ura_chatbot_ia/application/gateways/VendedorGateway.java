package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendedorGateway {
    Optional<Vendedor> consultarVendedor(String nome);

    List<Vendedor> listarPorUsuario(UUID idUsuario);

    List<Vendedor> listarComExcecao(String excecao);

    Vendedor salvar(Vendedor novoVendedor);

    Optional<Vendedor> consultarPorTelefone(String telefone);

    void deletar(Long idVendedor);

    Optional<Vendedor> consultarPorId(Long idVendedor);

    List<Vendedor> listarAtivos();
}
