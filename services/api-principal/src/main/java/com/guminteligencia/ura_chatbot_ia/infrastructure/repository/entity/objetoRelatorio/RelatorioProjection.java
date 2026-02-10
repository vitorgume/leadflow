package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.objetoRelatorio;

import java.time.LocalDateTime;

public interface RelatorioProjection {
    String getNome();
    String getTelefone();
    String getAtributosQualificacao();
    LocalDateTime getDataCriacao();
    String getNomeVendedor();
}
