package com.elotech.teste.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GoogleBooksRestClientConfig {
    
    @Bean
    public RestClient restClient(GoogleBooksConfig config) {
        return RestClient.builder()
                .baseUrl(config.getBaseUrl())
                .build();
    }
}
