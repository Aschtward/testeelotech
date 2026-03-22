package com.elotech.teste.domain.converters;

import com.elotech.teste.domain.valueobject.VoEmail;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VoEmailConverter implements AttributeConverter<VoEmail, String> {

    @Override
    public String convertToDatabaseColumn(VoEmail attribute) {
        return attribute.getValor();
    }

    @Override
    public VoEmail convertToEntityAttribute(String dbData) {
        return new VoEmail(dbData);
    }
}
