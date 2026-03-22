package com.elotech.teste.api.dto.usuario;

import com.elotech.teste.domain.entity.usuario.Usuario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.ZonedDateTime;

public record UsuarioDTO(
        Long id,
        @NotNull
        String nome,
        @NotNull
        String email,
        @NotNull
        @PastOrPresent
        ZonedDateTime dataCadastro,
        @NotNull
        String telefone
) {

    public static UsuarioDTO of(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail().getValor(),
                usuario.getDataCadastro().getValor(),
                usuario.getTelefone().getValor());
    }
}
