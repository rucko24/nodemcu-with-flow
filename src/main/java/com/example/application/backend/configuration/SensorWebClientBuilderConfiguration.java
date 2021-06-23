package com.example.application.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import static com.example.application.backend.services.Dht22Service.BASE_URL;

/**
 * WebClient builder with BASE_URL
 */
@Configuration
public class SensorWebClientBuilderConfiguration {

    @Bean
    public WebClient sensorWebClientBuilder(final WebClient.Builder webClient) {
        return webClient
                .baseUrl(BASE_URL)
                .build();
    }
}
