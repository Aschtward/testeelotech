package com.elotech.teste.api.controller.usuario;

import com.elotech.teste.api.dto.usuario.UsuarioDTO;

import java.time.ZonedDateTime;

public class UsuarioDataProvider {

    public static UsuarioDTO getUsuario() {
        return new UsuarioDTO(
                null,
                "Leonardo",
                "leoteste@gmail.com",
                ZonedDateTime.now(),
                "44991563680"
        );
    }

    public static UsuarioDTO getUsuarioSemCampos() {
        return new UsuarioDTO(
                null,
                "Leonardo",
                "leoteste@gmail.com",
                ZonedDateTime.now(),
                null
        );
    }
}
