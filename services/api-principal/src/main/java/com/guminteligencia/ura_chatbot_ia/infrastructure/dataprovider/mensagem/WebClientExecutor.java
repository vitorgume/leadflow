package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.mensagem;

import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetryBackoffSpec;

import java.util.Map;

@Component
@Slf4j
public class WebClientExecutor {

    private final WebClient webClient;
    private final RetryBackoffSpec retrySpec;

    // 1. INJEÇÃO CORRETA: Recebe o Builder e o Retry Global
    public WebClientExecutor(WebClient.Builder builder, RetryBackoffSpec retrySpec) {
        this.webClient = builder.build();
        this.retrySpec = retrySpec;
    }

    public String post(String uri, Object body, Map<String, String> headers, String errorMessage) {
        return execute(uri, body, headers, errorMessage, HttpMethod.POST);
    }

    public String execute(String uri, Object body, Map<String, String> headers, String errorMessage, HttpMethod method) {
        try {
            // 2. Criação do Spec inicial (com URL e Método)
            WebClient.RequestBodySpec spec = webClient.method(method).uri(uri);

            // 3. Configuração dos Headers
            spec.contentType(MediaType.APPLICATION_JSON); // Default
            if (headers != null) {
                headers.forEach(spec::header);
            }

            // 4. Definição do Body (se houver e o método permitir)
            WebClient.RequestHeadersSpec<?> headersSpec = spec;
            if (body != null && (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH)) {
                headersSpec = spec.bodyValue(body);
            }

            // 5. Execução
            return headersSpec.retrieve()
                    .onStatus(HttpStatusCode::isError, resp ->
                            resp.bodyToMono(String.class)
                                    .defaultIfEmpty("<empty-body>")
                                    .flatMap(bodyStr -> {
                                        String msg = String.format("%s | HTTP %d | Body: %s",
                                                errorMessage, resp.statusCode().value(), bodyStr);
                                        log.error(msg);
                                        return Mono.error(new DataProviderException(msg, null));
                                    })
                    )
                    .bodyToMono(String.class)
                    .retryWhen(retrySpec)
                    .doOnSuccess(r -> log.info("Response recebido da URI: {}", uri)) // Log menos verboso para segurança
                    .block();

        } catch (Exception e) {
            // Mantém a lógica de captura original para formatar a exceção
            String msg = String.format("%s | cause=%s", errorMessage, e.getMessage());
            log.error(msg, e);

            // Se a causa já for DataProviderException (do onStatus), relança ela mesma ou a causa raiz
            if (e instanceof DataProviderException) {
                throw (DataProviderException) e;
            }
            throw new DataProviderException(msg, e);
        }
    }
}
