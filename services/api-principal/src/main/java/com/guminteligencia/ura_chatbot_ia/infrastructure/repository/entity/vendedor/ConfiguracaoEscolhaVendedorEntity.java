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
    @Column(name = "id_configuracao_escolha_vendedor")
    private UUID id;

    @ManyToOne
    private UsuarioEntity usuario;

    @ManyToMany
    @JoinTable(
            name = "configuracao_vendedores",
            joinColumns = @JoinColumn(name = "id_configuracao_escolha_vendedor"),
            inverseJoinColumns = @JoinColumn(name = "id_vendedor")
    )
    private List<VendedorEntity> vendedores;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_configuracao_escolha_vendedor")
    List<CondicaoEntity> condicoes;

    private Integer prioridade;
}
