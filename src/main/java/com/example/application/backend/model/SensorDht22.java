package com.example.application.backend.model;

import lombok.Data;

/**
 *
 */
@Data
public class SensorDht22 {

    private String sensor;
    private String type;
    private String humidity;
    private String temperature;

}
