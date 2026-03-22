package com.elotech.teste.domain.converters;

import com.elotech.teste.domain.valueobject.VoTelefone;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VoTelefoneConverter implements AttributeConverter<VoTelefone, String> {
    @Override
    public String convertToDatabaseColumn(VoTelefone attribute) {
        return attribute.getValor();
    }

    @Override
    public VoTelefone convertToEntityAttribute(String dbData) {
        return new VoTelefone(dbData);
    }
}
