package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "Prompt")
@Table(name = "prompts_usuario")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PromptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private UsuarioEntity usuario;

    private String titulo;

    private String prompt;
}
