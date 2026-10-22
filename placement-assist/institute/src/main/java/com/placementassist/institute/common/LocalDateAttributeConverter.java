package com.placementassist.institute.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;

/**
 * Ensures clean conversion between LocalDate and SQLite TEXT/VARCHAR columns.
 * Handles both plain "YYYY-MM-DD" and timestamp strings "YYYY-MM-DD HH:mm:ss.SSS"
 * safely without relying on SQLite-JDBC's rigid FastDateParser.
 */
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate locDate) {
        return locDate == null ? null : locDate.toString();
    }

    @Override
    public LocalDate convertToEntityAttribute(String sqlString) {
        if (sqlString == null || sqlString.trim().isEmpty()) {
            return null;
        }
        String clean = sqlString.trim();
        if (clean.length() >= 10) {
            return LocalDate.parse(clean.substring(0, 10));
        }
        return LocalDate.parse(clean);
    }
}
