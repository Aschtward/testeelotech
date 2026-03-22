package com.elotech.teste.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoEmail {

    private String valor;

    public VoEmail(String valor) {
        if (!isEmailValido(valor)) throw new IllegalArgumentException("Formato de email inválido");
        this.valor = valor;
    }

    public boolean isEmailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other instanceof VoEmail) {
            return Objects.equals(valor, ((VoEmail) other).valor);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}
