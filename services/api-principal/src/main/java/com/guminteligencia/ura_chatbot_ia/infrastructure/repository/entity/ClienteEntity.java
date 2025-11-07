package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import com.guminteligencia.ura_chatbot_ia.domain.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "Cliente")
@Table(name = "clientes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cliente")
    private UUID id;
    private String nome;
    private String telefone;
    private String cpf;

    @Column(name = "consentimento_atendimento")
    private Boolean consentimentoAtendimnento;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "tipo_consulta")
    private TipoConsulta tipoConsulta;

    @Column(name = "dor_desejo_paciente")
    private String dorDesejoPaciente;


    @Column(name = "link_midia")
    private String linkMidia;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "preferencia_horario")
    private PreferenciaHorario preferenciaHorario;

    private boolean inativo;
}
