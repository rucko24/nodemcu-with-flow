package com.example.application.backend.model;

import lombok.Data;

/**
 * @author rubn
 */
@Data
public class VaadinServerTimestamp {
    private double value;
    private long timestamp;

    public VaadinServerTimestamp(long timestamp, double value) {
        this.timestamp = timestamp;
        if(value >= 0 && value <= 100) {// range sensor 0 to 100%RH
            this.value = value;
        }
    }

}