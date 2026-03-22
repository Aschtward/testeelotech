package com.elotech.teste.api.dto.livro;

import com.elotech.teste.domain.valueobject.VoData;

import java.time.ZonedDateTime;
import java.util.List;

public record VolumeDTO(
        String title,
        List<String> authors,
        String publishedDate,
        List<String> categories,
        List<IndustryIdentifierDTO> industryIdentifiers
) {

    public String getAutor() {
        if (authors == null || authors.isEmpty()) return "Desconhecido";
        return String.join(",", authors);
    }

    public List<String> getCategoria() {
        if (categories == null || categories.isEmpty()) return List.of("Sem categoria definida");
        return categories;
    }

    public String getIsbn() {
        if (industryIdentifiers == null) return "Sem informação";

        return industryIdentifiers.stream()
                .filter(id -> "ISBN_13".equals(id.type()) || "ISBN_10".equals(id.type()))
                .map(IndustryIdentifierDTO::identifier)
                .findFirst()
                .orElse("Sem informação");
    }

    public ZonedDateTime getDataPublicacao() {
        if (publishedDate == null) return VoData.hoje().getValor();
        try {
            if (publishedDate.length() == 4) {
                return ZonedDateTime.parse(publishedDate + "-01-01T00:00:00Z");
            }
            if (publishedDate.length() == 7) {
                return ZonedDateTime.parse(publishedDate + "-01T00:00:00Z");
            }
            return ZonedDateTime.parse(publishedDate + "T00:00:00Z");
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível inferir a data de publicação do livro");
        }
    }
}
