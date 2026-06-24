package com.sashimi.resume.infrastructure.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.YearMonth;

@Converter
public class YearMonthStringConverter
        implements AttributeConverter<YearMonth, String> {

    @Override
    public String convertToDatabaseColumn(
            YearMonth attribute
    ) {
        if (attribute == null) {
            return null;
        }

        return attribute.toString();
    }

    @Override
    public YearMonth convertToEntityAttribute(
            String databaseValue
    ) {
        if (databaseValue == null
                || databaseValue.isBlank()) {
            return null;
        }

        return YearMonth.parse(databaseValue);
    }
}