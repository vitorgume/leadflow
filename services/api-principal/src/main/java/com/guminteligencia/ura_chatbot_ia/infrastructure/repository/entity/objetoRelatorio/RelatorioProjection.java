package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.objetoRelatorio;

import java.time.LocalDateTime;

public interface RelatorioProjection {
    String getNome();
    String getTelefone();
    String getAtributosQualificacao(); // O Driver do MySQL costuma entregar JSON como String, então aqui ok
    LocalDateTime getDataCriacao();    // <--- AQUI A MÁGICA: O Spring converte o Timestamp do banco pra cá
    String getNomeVendedor();
}
