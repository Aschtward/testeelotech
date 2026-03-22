package com.elotech.teste.api.dto.livro;

import java.util.List;

public record GoogleBooksResponseDTO(
        List<GoogleBooksItensDTO> items
) {
}
