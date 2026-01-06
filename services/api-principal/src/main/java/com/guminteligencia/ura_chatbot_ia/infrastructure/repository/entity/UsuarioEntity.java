package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "Usuario")
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nome;
    private String telefone;
    private String senha;
    private String email;
}
