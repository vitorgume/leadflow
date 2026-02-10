package com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity;

import com.gumeinteligencia.api_intermidiaria.domain.PreferenciaHorario;
import com.gumeinteligencia.api_intermidiaria.domain.TipoConsulta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
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

    @Column(name = "atributos_qualificacao")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> atributosQualificacao;

    private boolean inativo;
}
