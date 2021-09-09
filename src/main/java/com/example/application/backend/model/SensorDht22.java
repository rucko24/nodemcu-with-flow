package com.example.application.backend.model;

import lombok.Builder;
import lombok.Value;

/**
 * @author rubn
 * @implSpec THREAD-SAFE
 */
@Value
@Builder
public class SensorDht22 {

    int id;
    String status;
    String sensor;
    String type;
    double humidity;
    double temperature;

}
