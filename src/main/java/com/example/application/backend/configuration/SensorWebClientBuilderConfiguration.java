package com.example.application.backend.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

import static com.example.application.backend.services.Dht22Service.BASE_URL;

/**
 * WebClient builder with BASE_URL
 */
@Configuration
public class SensorWebClientBuilderConfiguration {

    @Bean
    public WebClient sensorWebClientBuilder(final WebClient.Builder webClient) {
        //Create reactor netty HTTP client
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .doOnConnected((Connection connection) ->
                        connection.addHandlerLast(new ReadTimeoutHandler(2000, TimeUnit.MILLISECONDS)));
        //Use this configured http connector to build the web client
        return webClient
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(BASE_URL)
                .build();
    }
}
