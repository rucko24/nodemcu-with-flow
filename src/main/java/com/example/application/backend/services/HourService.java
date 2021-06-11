package com.example.application.backend.services;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * @author rubn
 * Show hour with pattern HH:mm:ss a
 */
@Service
public class HourService {

    public String getHour() {
        return DateTimeFormatter.ofPattern("HH:mm:ss a")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());
    }

    public long getLocalDateTimeNow() {
        return LocalDateTime.parse(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli();
    }

}
