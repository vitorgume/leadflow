package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import com.guminteligencia.ura_chatbot_ia.domain.TipoContato;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "OutroContato")
@Table(name = "outros_contatos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OutroContatoEntitySql {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_outro_contato")
    private UUID id;
    private String nome;
    private String telefone;
    private String descricao;

    @Column(name = "tipo_contato")
    @Enumerated(EnumType.STRING)
    private TipoContato tipoContato;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_usuario",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_outro_contato_usuario")
    )
    private UsuarioEntity usuario;
}
