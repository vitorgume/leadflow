package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "Membro")
@Table(name = "membros")
@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class MembroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_membro")
    private UUID id;
    private String nome;
    private String telefone;
    @ManyToOne
    private UsuarioEntity usuario;
}
