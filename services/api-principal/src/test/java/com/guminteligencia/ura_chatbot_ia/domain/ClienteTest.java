package com.guminteligencia.ura_chatbot_ia.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class ClienteTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Nome teste")
                .telefone("000000000000")
                .inativo(false)
                .build();
    }

    @Test
    void deveAlterarTodosOsDados() {
        Cliente novosDados = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Nome teste 2")
                .telefone("00000000001")
                .inativo(true)
                .build();

        cliente.setDados(novosDados);

        Assertions.assertNotEquals(cliente.getId(), novosDados.getId());
        Assertions.assertEquals(cliente.getNome(), novosDados.getNome());
        Assertions.assertNotEquals(cliente.getTelefone(), novosDados.getTelefone());
        Assertions.assertFalse(cliente.isInativo());
    }
}