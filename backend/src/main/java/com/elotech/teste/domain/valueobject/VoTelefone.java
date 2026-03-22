package com.elotech.teste.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoTelefone {

    private String valor;

    public VoTelefone(String valor) {
        String valorNormalizado = normalizar(valor);
        if (!isValido(valorNormalizado)) throw new IllegalArgumentException("Valor de telefone inválido");
        this.valor = valorNormalizado;
    }

    private String normalizar(String valor) {
        if (valor == null) return null;

        String valorNormalizado = valor.replaceAll("\\D", "");

        if (valorNormalizado.length() == 11) return "+55" + valorNormalizado;
        if (valorNormalizado.length() == 13 && valorNormalizado.startsWith("55")) return "+" + valorNormalizado;

        return valorNormalizado;
    }

    private boolean isValido(String telefone) {
        return telefone != null && telefone.matches("^\\+55\\d{11}$");
    }
}
