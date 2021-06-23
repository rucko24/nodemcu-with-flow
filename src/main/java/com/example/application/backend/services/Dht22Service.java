package com.example.application.backend.services;

import com.example.application.backend.services.charts.ApexChartService;
import com.example.application.backend.model.SensorDht22;
import com.example.application.backend.services.grid.SensorDht22GridServices;
import com.github.appreciated.apexcharts.ApexCharts;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.*;

import static com.example.application.backend.services.charts.ApexChartService.HUMIDITY;
import static com.example.application.backend.services.charts.ApexChartService.TEMPERATURE;


/**
 * @author rubn
 */
@Log4j2
@Service
public class Dht22Service {

    public static final String BASE_URL = "http://192.168.1.128:8081/async-esp8285/api/v1/";
    public static final String LED = "led";
    public static final String DHT_22 = "dht22";
    public static final String LED_ID = "id";
    public static final int LED_ID_VALUE = 2;
    public static final String STATUS = "status";
    public static final String ON = "on";
    public static final String OFF = "off";

    private TimestampService timestampService;
    private WebClient webClient;

    @Autowired
    public Dht22Service(final TimestampService timestampService, final WebClient webClient) {
        this.timestampService = timestampService;
        this.webClient = webClient;
    }

    public void readDht22Sensor(final UI ui, final ApexCharts apexCharts,
                                final ApexChartService service,
                                final H2 h2,final H2 h2Humidity,
                                final SensorDht22GridServices sensorDht22GridServices) {

        webClient
                .get()
                .uri(DHT_22)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SensorDht22.class)
                .delaySubscription(Duration.ofSeconds(2))
                .repeat()
                .doOnError(error -> ui.access(() -> Notification.show(error.getMessage())))
                .subscribe(sensorDht22 -> {
                    ui.access(() -> {
                        sensorDht22GridServices.setData(sensorDht22);
                        h2.setText(String.valueOf(sensorDht22.getTemperature()));
                        h2Humidity.setText(String.valueOf(sensorDht22.getHumidity()));
                        final var timestampHumidities = timestampService.getTimestampHumidities(sensorDht22.getHumidity());
                        apexCharts.setLabels(service.getApexChartsLabels(timestampHumidities));
                        final var listCoordinate = timestampService.getTimestampHumidities(sensorDht22.getHumidity());
                        var seriesHumidities = service.getApexChartsCoordinateSeries(HUMIDITY,listCoordinate);

                        final var timestampTemperatures = timestampService.getTimestampTemperatures(sensorDht22.getTemperature());
                        apexCharts.setLabels(service.getApexChartsLabels(timestampTemperatures));
                        final var listCoordinate2 = timestampService.getTimestampTemperatures(sensorDht22.getTemperature());
                        var seriesTemperatures = service.getApexChartsCoordinateSeries(TEMPERATURE,listCoordinate2);

                        apexCharts.updateSeries(seriesHumidities, seriesTemperatures);
                    });
                });
    }

}
