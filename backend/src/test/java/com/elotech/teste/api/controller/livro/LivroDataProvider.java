package com.elotech.teste.api.controller.livro;

import com.elotech.teste.api.dto.livro.LivroDTO;

import java.time.ZonedDateTime;
import java.util.List;

public class LivroDataProvider {

    public static LivroDTO getLivro() {
        return new LivroDTO(
                null,
                "Livro de teste",
                "Leonardo",
                "16313125431",
                ZonedDateTime.now(),
                List.of("Computação")
        );
    }

    public static LivroDTO getLivroSemCampos() {
        return new LivroDTO(
                null,
                null,
                "Leonardo",
                "16313125431",
                ZonedDateTime.now(),
                List.of("Computação")
        );
    }
}
