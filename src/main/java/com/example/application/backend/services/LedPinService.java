package com.example.application.backend.services;

import com.example.application.backend.model.SensorDht22;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.function.Function;

import static com.example.application.views.dht22am2302.DHT22AM2302View.BASE_URL;

@Service
public class LedPinService {

    public void highLowPin(final UI ui, Function<UriBuilder, URI> functionQueryParameters) {
        WebClient.create(BASE_URL)
                .put()
                .uri(functionQueryParameters)
                .accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(SensorDht22.class)
                .delaySubscription(Duration.ofSeconds(2))
                .repeat()
                .doOnError(e -> {
                    ui.access(() -> {
                        Notification.show(e.getMessage());
                    });
                })
                .subscribe(sensorDht22 -> {
                    ui.access(() -> {

                    });
                });
    }

}
