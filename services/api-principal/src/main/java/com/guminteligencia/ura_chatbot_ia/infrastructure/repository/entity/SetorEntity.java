package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity(name = "Setor")
@Table(name = "setores")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SetorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_setor")
    private UUID id;
    private String nome;
    private String descricao;

    @OneToMany
    @JoinColumn(name = "id_membro")
    private List<MembroEntity> membros;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @ManyToOne
    private UsuarioEntity usuario;
}
