package com.placementassist.shared.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Utility for ISO-8601 date/time formatting and parsing.
 */
public final class DateUtil {

    public static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateUtil() {
    }

    public static String formatLocalDate(LocalDate date) {
        return date != null ? date.format(ISO_DATE_FORMATTER) : null;
    }

    public static LocalDate parseLocalDate(String text) {
        return (text != null && !text.isBlank()) ? LocalDate.parse(text, ISO_DATE_FORMATTER) : null;
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(ISO_DATE_TIME_FORMATTER) : null;
    }

    public static LocalDateTime parseLocalDateTime(String text) {
        return (text != null && !text.isBlank()) ? LocalDateTime.parse(text, ISO_DATE_TIME_FORMATTER) : null;
    }

    public static LocalDateTime fromInstant(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) : null;
    }
}
