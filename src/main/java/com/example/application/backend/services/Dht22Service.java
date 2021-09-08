package com.example.application.backend.services;

import com.example.application.backend.services.charts.ApexChartService;
import com.example.application.backend.model.SensorDht22;
import com.example.application.backend.services.grid.SensorDht22GridServices;
import com.example.application.util.NotificationsUtils;
import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;

import static com.example.application.backend.services.charts.ApexChartService.HUMIDITY;
import static com.example.application.backend.services.charts.ApexChartService.TEMPERATURE;

/**
 * @author rubn
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class Dht22Service implements NotificationsUtils {

    public static final String BASE_URL = "http://192.168.1.128:8081/async-esp8285/api/v1/";
    public static final String LED = "led";
    public static final String DHT_22 = "dht22";
    public static final String LED_ID = "id";
    public static final int LED_ID_VALUE = 2;
    public static final String STATUS = "status";
    public static final String ON = "on";
    public static final String OFF = "off";

    private final TimestampService timestampService;
    private final WebClient webClient;

    public void readDht22Sensor(final UI ui, final ApexCharts apexCharts,
                                final ApexChartService service,
                                final H2 h2, final H2 h2Humidity,
                                final SensorDht22GridServices sensorDht22GridServices) {

        final Flux<SensorDht22> publisherSensorDht22 = this.webClient
                .get()
                .uri(DHT_22)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SensorDht22.class)
                .delaySubscription(Duration.ofSeconds(2))
                .repeat()
                .doOnError((Throwable error) -> {
                    ui.access(() -> this.showError(error.getMessage()));
                })
                //resubscribe when there is an error signal.
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofMillis(500)));

        this.subscribeSensor(publisherSensorDht22, ui, apexCharts, service, h2, h2Humidity, sensorDht22GridServices);
    }

    /**
     *
     * Make Subscription inside
     *
     * @param publisher
     * @param ui
     * @param apexCharts
     * @param service
     * @param h2Temperature
     * @param h2Humidity
     * @param sensorDht22GridServices
     */
    private void subscribeSensor(final Flux<SensorDht22> publisher,
                                 final UI ui, final ApexCharts apexCharts,
                                 final ApexChartService service,
                                 final H2 h2Temperature, final H2 h2Humidity,
                                 final SensorDht22GridServices sensorDht22GridServices) {

        publisher
                .subscribe(sensorDht22 -> {
                    ui.access(() -> {
                        sensorDht22GridServices.setData(sensorDht22);

                        Series<Object> seriesHumidities = this.humidity(sensorDht22, apexCharts, service, h2Humidity);

                        Series<Object> seriesTemperatures = this.temperature(sensorDht22, apexCharts, service, h2Temperature);

                        //Aqui pintamos nuestra chart con dos series.
                        apexCharts.updateSeries(seriesHumidities, seriesTemperatures);
                    });
                });
    }

    /**
     * Display humidity
     *
     * @param sensorDht22
     * @param apexCharts
     * @param service
     * @param h2Humidity
     * @return Series<Object>
     */
    private Series<Object> humidity(SensorDht22 sensorDht22, final ApexCharts apexCharts,
                                    final ApexChartService service,
                                    final H2 h2Humidity) {

        h2Humidity.setText(String.valueOf(sensorDht22.getHumidity()));
        final var timestampHumidities = timestampService.getTimestampHumidities(sensorDht22.getHumidity());
        apexCharts.setLabels(service.getApexChartsLabels(timestampHumidities));
        final var listCoordinate = timestampService.getTimestampHumidities(sensorDht22.getHumidity());
        return service.getApexChartsCoordinateSeries(HUMIDITY, listCoordinate);
    }

    /**
     * Display temperature
     * @param sensorDht22
     * @param apexCharts
     * @param service
     * @param h2Temperature
     *
     * @return Series<Object>
     */
    private Series<Object> temperature(SensorDht22 sensorDht22, final ApexCharts apexCharts,
                             final ApexChartService service,
                             final H2 h2Temperature) {

        h2Temperature.setText(String.valueOf(sensorDht22.getTemperature()));
        final var timestampTemperatures = timestampService.getTimestampTemperatures(sensorDht22.getTemperature());
        apexCharts.setLabels(service.getApexChartsLabels(timestampTemperatures));
        final var listCoordinate2 = timestampService.getTimestampTemperatures(sensorDht22.getTemperature());
        return service.getApexChartsCoordinateSeries(TEMPERATURE, listCoordinate2);
    }

}
