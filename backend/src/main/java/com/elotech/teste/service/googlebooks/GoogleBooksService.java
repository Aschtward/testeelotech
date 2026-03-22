package com.elotech.teste.service.googlebooks;

import com.elotech.teste.api.dto.livro.GoogleBooksResponseDTO;
import com.elotech.teste.infra.client.GoogleBooksRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleBooksService {

    private final GoogleBooksRestClient googleBooksRestClient;

    public GoogleBooksResponseDTO buscarLivros(String nome, int pagina) {
        return googleBooksRestClient.buscar(nome, pagina);
    }
}
