package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.IntegracaoKommoGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.kommo.PayloadKommo;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactsResponse;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetryBackoffSpec;

import java.net.URI;
import java.util.Comparator;
import java.util.Optional;

@Component
@Slf4j
public class IntegracaoKommoDataProvider implements IntegracaoKommoGateway {

    private final WebClient webClient;
    private final RetryBackoffSpec retrySpec;

    public static final String MENSAGEM_ERRO_CONSULTAR_LEAD = "Erro ao consultar lead pelo telefone.";
    public static final String MENSAGEM_ERRO_ATUALIZAR_CARD = "Erro ao atualizar card.";

    // Injetamos o Builder (já com timeout global) e o Retry Global
    public IntegracaoKommoDataProvider(WebClient.Builder builder,
                                       RetryBackoffSpec retrySpec) { // Use a variável correta do properties
        this.retrySpec = retrySpec;

        this.webClient = builder
                .defaultHeader(HttpHeaders.ACCEPT, "application/hal+json") // Header específico do Kommo
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(kommoErrorFilter()) // Filtro de erro específico do Kommo
                .build();
    }

    @Override
    public Optional<Integer> consultaLeadPeloTelefone(String telefoneE164, String acessToken, String crmUrl) {
        String normalized = normalizeE164(telefoneE164);

        try {
            URI uriCompleta = UriComponentsBuilder.fromUriString(crmUrl)
                    .path("/contacts")
                    .queryParam("query", normalized)
                    .queryParam("with", "leads")
                    .queryParam("limit", 50)
                    .build()
                    .toUri();

            ContactsResponse contacts = webClient.get()
                    .uri(uriCompleta)
                    .headers(h -> h.setBearerAuth(acessToken))
                    .retrieve()
                    .bodyToMono(ContactsResponse.class)
                    .retryWhen(retrySpec) // Usa a política de retry global
                    .block(); // Bloqueia para manter contrato síncrono

            return extrairLeadIdDoContato(contacts);

        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_LEAD, ex);
            // Lança a exceção de negócio mantendo a causa original
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_LEAD, ex);
        }
    }

    @Override
    public void atualizarCard(PayloadKommo body, Integer idLead, String acessToken, String crmUrl) {
        try {
            URI uriCompleta = UriComponentsBuilder.fromUriString(crmUrl)
                    .path("/leads/{id}")
                    .buildAndExpand(idLead)
                    .toUri();

            webClient.patch()
                    .uri(uriCompleta)
                    .bodyValue(body)
                    .headers(h -> h.setBearerAuth(acessToken))
                    .retrieve()
                    .toBodilessEntity()
                    .retryWhen(retrySpec)
                    .block();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_ATUALIZAR_CARD, ex);
            throw new DataProviderException(MENSAGEM_ERRO_ATUALIZAR_CARD, ex);
        }
    }

    // --- Métodos Auxiliares ---

    private Optional<Integer> extrairLeadIdDoContato(ContactsResponse contacts) {
        if (contacts == null || contacts.getEmbedded() == null || contacts.getEmbedded().getContacts() == null) {
            return Optional.empty();
        }

        return contacts.getEmbedded().getContacts().stream()
                // Filtra contatos que tenham leads atrelados
                .filter(c -> c.getEmbedded() != null && c.getEmbedded().getLeads() != null && !c.getEmbedded().getLeads().isEmpty())
                // Pega o contato mais recentemente atualizado
                .max(Comparator.comparing(ContactDto::getUpdatedAt, Comparator.nullsFirst(Long::compareTo)))
                // Pega o ID do primeiro lead desse contato
                .map(c -> c.getEmbedded().getLeads().get(0).getId());
    }

    private static String normalizeE164(String fone) {
        String f = fone == null ? "" : fone.trim();
        f = f.replaceAll("[^\\d+]", "");
        if (!f.startsWith("+")) f = "+" + f;
        return f;
    }

    // Filtro específico para tratar erros do Kommo (vinda da antiga KommoConfig)
    private ExchangeFilterFunction kommoErrorFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(res -> {
            if (res.statusCode().isError()) {
                // Se for 204 (No Content), às vezes o Kommo retorna isso em buscas vazias, não é erro
                if (res.statusCode().value() == 204) {
                    return Mono.just(res);
                }
                return res.bodyToMono(String.class)
                        .defaultIfEmpty("Sem corpo de erro")
                        .flatMap(body -> Mono.error(new RuntimeException("Kommo API Error [" + res.statusCode() + "]: " + body)));
            }
            return Mono.just(res);
        });
    }
}
