package com.elotech.teste.api.dto.livro;

import com.elotech.teste.domain.entity.livro.Livro;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

public record LivroDTO(
        Long id,
        @NotNull
        String titulo,
        @NotNull
        String autor,
        @NotNull
        String isbn,
        @NotNull
        ZonedDateTime dataPublicacao,
        @NotNull
        @NotEmpty
        List<String> categoria
) {

    public static LivroDTO of(Livro livro) {
        return new LivroDTO(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getDataPublicacaoValor(),
                livro.getCategorias()
        );
    }

    public static LivroDTO of(VolumeDTO volumeDTO) {
        return new LivroDTO(
                null,
                volumeDTO.title(),
                volumeDTO.getAutor(),
                volumeDTO.getIsbn(),
                volumeDTO.getDataPublicacao(),
                volumeDTO.getCategoria()
        );
    }
}
