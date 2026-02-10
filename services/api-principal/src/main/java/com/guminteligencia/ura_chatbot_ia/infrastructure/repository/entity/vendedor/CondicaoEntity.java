package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConectorLogico;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "Condicao")
@Table(name = "condicoes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CondicaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_condicao")
    private UUID id;

    private String campo;

    @Enumerated(EnumType.STRING)
    private OperadorLogico operadorLogico;

    private String valor;

    @Enumerated(EnumType.STRING)
    private ConectorLogico conectorLogico;
}
