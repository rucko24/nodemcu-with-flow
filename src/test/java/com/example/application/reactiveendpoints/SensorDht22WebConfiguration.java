package com.example.application.reactiveendpoints;

import com.example.application.backend.model.SensorDht22;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 *
 */
@Configuration
class SensorDht22WebConfiguration {

    public static final String TEST_URL_BASE = "/async-esp8285/api/v1/dht22";
    public static final String DHT_22 = "DHT-22";
    public static final String AM2302 = "AM2302";
    public static final double HUMIDITY = 50.5;
    public static final double TEMPERATURE = 25.4;

    /**
     * Define functional reactive HTTPS routes
     *
     * @return RouterFunction
     */
    @Bean
    public RouterFunction<ServerResponse> routes() {
        final SensorDht22 sensorDht22 = SensorDht22.builder()
                .sensor(DHT_22)
                .type(AM2302)
                .humidity(HUMIDITY)
                .temperature(TEMPERATURE)
                .build();

        return RouterFunctions.route(RequestPredicates.GET(TEST_URL_BASE),
                serverRequest -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(sensorDht22), SensorDht22.class));
    }
}
