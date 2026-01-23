package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
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

    @Column(name = "telefone_conectado")
    private String telefoneConectado;

    @Column(name = "atributos_qualificacao")
    private Map<String, Object> atributosQualificacao;

    @Column(name = "configuracao_crm")
    @Embedded
    private ConfiguracaoCrmEntity configuracaoCrm;

    @Column(name = "mensagem_direcionamento_vendedor")
    private String mensagemDirecionamentoVendedor;

    @Column(name = "mensagem_recontato_g1")
    private String mensagemRecontatoG1;

    @Column(name = "whatsapp_token")
    private String whatsappToken;

    @Column(name = "whatsapp_id_instance")
    private String whatsappIdInstance;

    @Column(name = "agente_api_key")
    private String agenteApiKey;
}
