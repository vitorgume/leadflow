package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Prioridade;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.VendedorDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VendedorMapperTest {

    private Vendedor vendedorDomain;
    private VendedorDto vendedorDto;

    @BeforeEach
    void setUp() {
        vendedorDomain = Vendedor.builder()
                .id(1L)
                .nome("Nome teste")
                .telefone("554432165498778")
                .inativo(false)
                .prioridade(new Prioridade(1, true))
                .idVendedorCrm(1010)
                .padrao(true)
                .build();

        vendedorDto = VendedorDto.builder()
                .id(2L)
                .nome("Nome teste 2")
                .telefone("554432165498779")
                .inativo(true)
                .prioridade(new Prioridade(2, false))
                .idVendedorCrm(2020)
                .padrao(false)
                .build();
    }

    @Test
    void deveRetornarDomain() {
        Vendedor vendedorTeste = VendedorMapper.paraDomain(vendedorDto);

        Assertions.assertEquals(vendedorDto.getId(), vendedorTeste.getId());
        Assertions.assertEquals(vendedorDto.getNome(), vendedorTeste.getNome());
        Assertions.assertEquals(vendedorDto.getTelefone(), vendedorTeste.getTelefone());
        Assertions.assertEquals(vendedorDto.getInativo(), vendedorTeste.getInativo());
        Assertions.assertEquals(vendedorDto.getPrioridade(), vendedorTeste.getPrioridade());
        Assertions.assertEquals(vendedorDto.getIdVendedorCrm(), vendedorTeste.getIdVendedorCrm());
        Assertions.assertEquals(vendedorDto.getPadrao(), vendedorTeste.getPadrao());
    }

    @Test
    void deveRetornarDto() {
        VendedorDto vendedorTeste = VendedorMapper.paraDto(vendedorDomain);

        Assertions.assertEquals(vendedorDomain.getId(), vendedorTeste.getId());
        Assertions.assertEquals(vendedorDomain.getNome(), vendedorTeste.getNome());
        Assertions.assertEquals(vendedorDomain.getTelefone(), vendedorTeste.getTelefone());
        Assertions.assertEquals(vendedorDomain.getInativo(), vendedorTeste.getInativo());
        Assertions.assertEquals(vendedorDomain.getPrioridade(), vendedorTeste.getPrioridade());
        Assertions.assertEquals(vendedorDomain.getIdVendedorCrm(), vendedorTeste.getIdVendedorCrm());
        Assertions.assertEquals(vendedorDomain.getPadrao(), vendedorTeste.getPadrao());
    }
}
