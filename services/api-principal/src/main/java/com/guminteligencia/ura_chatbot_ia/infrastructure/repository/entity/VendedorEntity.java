package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import com.guminteligencia.ura_chatbot_ia.domain.Prioridade;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Vendedor")
@Table(name = "vendedores")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class VendedorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vendedor")
    private Long id;
    private String nome;
    private String telefone;
    private Boolean inativo;

    @Column(name = "id_vendedor_crm")
    private Integer idVendedorCrm;

    @Embedded
    private Prioridade prioridade;

    private Boolean padrao;
}
