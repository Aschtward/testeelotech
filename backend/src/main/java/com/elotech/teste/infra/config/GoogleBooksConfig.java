package com.elotech.teste.infra.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google.books")
@Getter
@Setter
public class GoogleBooksConfig {
    private String baseUrl;
    private String key;
    private int maxResults;
}
