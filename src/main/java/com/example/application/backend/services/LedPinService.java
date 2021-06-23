package com.example.application.backend.services;

import com.example.application.backend.model.SensorDht22;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.function.Function;

import static com.example.application.backend.services.Dht22Service.BASE_URL;

/**
 * @author rubn
 */
@Service
public class LedPinService {

    private WebClient webClient;

    @Autowired
    public LedPinService(final WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     *
     * @param ui UI
     * @param functionQueryParameters ?id=2&status={on/off}
     */
    public void highLowPin(final UI ui, Function<UriBuilder, URI> functionQueryParameters) {
        this.webClient
                .put()
                .uri(functionQueryParameters)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SensorDht22.class)
                .doOnError(error -> ui.access(() -> Notification.show(error.getMessage())))
                .subscribe(sensorDht22 -> {
                    ui.access(() -> {
                        Notification.show(sensorDht22.getStatus());
                    });
                });
    }

}
