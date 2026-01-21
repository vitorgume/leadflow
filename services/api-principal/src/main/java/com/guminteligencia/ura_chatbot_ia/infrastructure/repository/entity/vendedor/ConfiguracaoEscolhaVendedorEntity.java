package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity(name = "ConfiguracaoEscolhaVendedor")
@Table(name = "configuracoes_escolha_vendedor")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ConfiguracaoEscolhaVendedorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private UsuarioEntity usuario;

    @ManyToOne
    private VendedorEntity vendedor;

    @OneToMany
    List<CondicaoEntity> condicoes;

    private Integer prioridade;
}
