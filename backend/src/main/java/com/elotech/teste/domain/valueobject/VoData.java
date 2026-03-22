package com.elotech.teste.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoData implements Comparable<VoData> {

    private ZonedDateTime valor;

    public VoData(ZonedDateTime valor) {
        this.valor = valor;
    }

    public static VoData hoje() {
        return new VoData(ZonedDateTime.now());
    }

    public static VoData maisDias(VoData voData, Long dias) {
        if (dias == null) return new VoData(voData.valor);
        return new VoData(voData.valor.plusDays(dias));
    }

    public static VoData menosDias(VoData voData, Long dias) {
        return new VoData(voData.valor.minusDays(dias));
    }

    public boolean isAntesDe(VoData data) {
        return this.valor.isBefore(data.valor);
    }

    public boolean isDepoisDe(VoData data) {
        if (data == null) return true;
        return this.valor.isAfter(data.valor);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other instanceof VoData) {
            return Objects.equals(valor, ((VoData) other).valor);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public int compareTo(VoData o) {
        return Comparator.comparing(VoData::getValor).compare(this, o);
    }
}
