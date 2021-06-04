package com.example.application.backend.services;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Show hour with pattern H:mm:ss
 */
@Service
public class HourService {

    public String getHour() {
        return DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());
    }

}
