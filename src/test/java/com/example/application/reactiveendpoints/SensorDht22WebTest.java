package com.example.application.reactiveendpoints;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.example.application.reactiveendpoints.SensorDht22WebConfiguration.*;

@WebFluxTest
@Import(SensorDht22WebConfiguration.class)
@DisplayName("< Testing endpoint of SensorDht22 /async-esp8285/api/v1/dht22>")
class SensorDht22WebTest {

    @Autowired
    private WebTestClient client;

    @Test
    @DisplayName("Get sensor name, type, humidity and temperature")
    void getSensorDht22() {
        this.client
                .get()
                .uri(TEST_URL_BASE)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.sensor").isEqualTo(DHT_22)
                .jsonPath("$.type").isEqualTo(AM2302)
                .jsonPath("$.humidity").isEqualTo(HUMIDITY)
                .jsonPath("$.temperature").isEqualTo(TEMPERATURE);
    }

}
