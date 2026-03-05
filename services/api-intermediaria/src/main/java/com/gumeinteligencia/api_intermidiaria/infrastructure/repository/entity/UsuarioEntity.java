package com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity;

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
    private String email;

    @Column(name = "telefone_conectado")
    private String telefoneConectado;

    @Column(name = "software_ligado")
    private Boolean softwareLigado;
}
