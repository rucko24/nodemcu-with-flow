package com.example.application.backend.model;

import lombok.Data;

/**
 * The SensorDht22Mapper is response from ESP8285
 */
@Data
public class SensorDht22Mapper {
    private int id;
    private String status;
    private String sensor;
    private String type;
    private double humidity;
    private double temperature;
}
