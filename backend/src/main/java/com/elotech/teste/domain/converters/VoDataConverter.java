package com.elotech.teste.domain.converters;

import com.elotech.teste.domain.valueobject.VoData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.ZonedDateTime;

@Converter(autoApply = true)
public class VoDataConverter implements AttributeConverter<VoData, ZonedDateTime> {
    @Override
    public ZonedDateTime convertToDatabaseColumn(VoData attribute) {
        return attribute.getValor();
    }

    @Override
    public VoData convertToEntityAttribute(ZonedDateTime dbData) {
        return new VoData(dbData);
    }
}
