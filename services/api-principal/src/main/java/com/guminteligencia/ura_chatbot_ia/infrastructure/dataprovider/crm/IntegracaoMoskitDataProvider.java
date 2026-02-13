package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.crm;

import com.guminteligencia.ura_chatbot_ia.application.gateways.crm.IntegracaoMoskitGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.ContatoMoskitDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.PayloadMoskit;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.ResponseContatoDto;
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

@Component
@Slf4j
public class IntegracaoMoskitDataProvider implements IntegracaoMoskitGateway {

    private final WebClient webClient;
    private final RetryBackoffSpec retryBackoffSpec;

    private final String MENSAGEM_ERRO_CRIAR_NEGOCIO_MOSKIT = "Erro ao criar negócio no moskit.";
    private final String MENSAGEM_ERRO_CRIAR_CONTATO_MOSKIT = "Erro ao criar contato no moskit.";

    public IntegracaoMoskitDataProvider(WebClient.Builder builder,
                                       RetryBackoffSpec retrySpec) { // Use a variável correta do properties
        this.retryBackoffSpec = retrySpec;

        this.webClient = builder
                .defaultHeader(HttpHeaders.ACCEPT, "application/hal+json") // Header específico do Kommo
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(moskitErrorFilter()) // Filtro de erro específico do Kommo
                .build();
    }

    @Override
    public void criarNegocio(PayloadMoskit payloadMoskit, String acessToken, String crmUrl) {
        try {
            URI uriCompleta = UriComponentsBuilder.fromUriString(crmUrl)
                    .path("/deals")
                    .build()
                    .toUri();

            webClient.post()
                    .uri(uriCompleta)
                    .bodyValue(payloadMoskit)
                    .headers(h -> h.set("apikey", acessToken))
                    .retrieve()
                    .toBodilessEntity()
                    .retryWhen(retryBackoffSpec)
                    .block();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CRIAR_NEGOCIO_MOSKIT, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CRIAR_NEGOCIO_MOSKIT, ex.getCause());
        }
    }

    @Override
    public Integer criarContato(ContatoMoskitDto contatoMoskitDto, String acessToken, String crmUrl) {
        try {
            URI uriCompleta = UriComponentsBuilder.fromUriString(crmUrl)
                    .path("/contacts")
                    .build()
                    .toUri();

            ResponseContatoDto response = webClient.post()
                    .uri(uriCompleta)
                    .bodyValue(contatoMoskitDto)
                    .headers(h -> h.set("apikey", acessToken))
                    .retrieve()
                    .bodyToMono(ResponseContatoDto.class)
                    .retryWhen(retryBackoffSpec)
                    .block();

            return response != null ? response.getId() : null;
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CRIAR_CONTATO_MOSKIT, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CRIAR_CONTATO_MOSKIT, ex.getCause());
        }
    }

    private ExchangeFilterFunction moskitErrorFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(res -> {
            if (res.statusCode().isError()) {
                // Se for 204 (No Content), às vezes o Kommo retorna isso em buscas vazias, não é erro
                if (res.statusCode().value() == 204) {
                    return Mono.just(res);
                }
                return res.bodyToMono(String.class)
                        .defaultIfEmpty("Sem corpo de erro")
                        .flatMap(body -> Mono.error(new RuntimeException("Moskit API Error [" + res.statusCode() + "]: " + body)));
            }
            return Mono.just(res);
        });
    }
}
