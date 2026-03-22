package com.elotech.teste.infra.client;

import com.elotech.teste.api.dto.livro.GoogleBooksResponseDTO;
import com.elotech.teste.infra.config.GoogleBooksConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleBooksRestClient {

    private final RestClient restClient;
    private final GoogleBooksConfig config;

    public GoogleBooksResponseDTO buscar(String query, int pagina) {
        int startIndex = pagina * config.getMaxResults();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", query)
                        .queryParam("key", config.getKey())
                        .queryParam("maxResults", config.getMaxResults())
                        .queryParam("startIndex", startIndex)
                        .build())
                .retrieve()
                .body(GoogleBooksResponseDTO.class);
    }
}
