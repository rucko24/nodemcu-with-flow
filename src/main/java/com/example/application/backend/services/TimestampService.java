package com.example.application.backend.services;

import com.example.application.backend.model.VaadinServerTimestamp;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author rubn
 */
@Service
public class TimestampService {
    private final List<VaadinServerTimestamp> listHumidities = new CopyOnWriteArrayList<>();
    private final List<VaadinServerTimestamp> listTemperatures = new CopyOnWriteArrayList<>();

    public List<VaadinServerTimestamp> getTimestampHumidities(final double mNextDouble) {
        final var vaadinServerTimestamp = new VaadinServerTimestamp(Instant.now().toEpochMilli(), mNextDouble);
        if (listHumidities.size() <= 10) {
            listHumidities.add(vaadinServerTimestamp);
        } else {
            listHumidities.remove(0);
        }
        return listHumidities;
    }

    public List<VaadinServerTimestamp> getTimestampTemperatures(final double mNextDouble) {
        final var vaadinServerTimestamp = new VaadinServerTimestamp(Instant.now().toEpochMilli(), mNextDouble);
        if (listTemperatures.size() <= 10) {
            listTemperatures.add(vaadinServerTimestamp);
        } else {
            listTemperatures.remove(0);
        }
        return listTemperatures;
    }

}
