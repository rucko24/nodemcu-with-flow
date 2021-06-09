package com.example.application.backend.model;

import lombok.Data;

/**
 * @author rubn
 */
@Data
public class SensorDht22 {

    private int id;
    private String status;
    private String sensor;
    private String type;
    private double humidity;
    private double temperature;

}
