package com.guminteligencia.ura_chatbot_ia.domain;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VendedorTest {

    private Vendedor vendedor;

//    @BeforeEach
//    void setUp() {
//        vendedor = Vendedor.builder()
//                .id(1L)
//                .nome("Nome teste")
//                .telefone("000000000000")
//                .inativo(false)
//                .prioridade(new Prioridade(1, true))
//                .build();
//    }
//
//    @Test
//    void deveAlterarDadosDeVendedor() {
//        Vendedor novosDados = Vendedor.builder()
//                .id(2L)
//                .nome("Nome teste 2")
//                .telefone("000000000001")
//                .inativo(true)
//                .prioridade(new Prioridade(0, false))
//                .build();
//
//        vendedor.setDados(novosDados);
//
//        Assertions.assertNotEquals(vendedor.getId(), novosDados.getId());
//        Assertions.assertEquals(vendedor.getNome(), novosDados.getNome());
//        Assertions.assertEquals(vendedor.getTelefone(), novosDados.getTelefone());
//        Assertions.assertTrue(vendedor.getInativo());
//        Assertions.assertEquals(vendedor.getPrioridade(), novosDados.getPrioridade());
//    }
}