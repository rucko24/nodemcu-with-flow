package com.example.application.backend.services;

import com.example.application.backend.model.SensorDht22Mapper;
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
import java.util.Optional;

import static com.example.application.backend.services.charts.ApexChartService.HUMIDITY;
import static com.example.application.backend.services.charts.ApexChartService.TEMPERATURE;

/**
 * @author rubn
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class Dht22Service implements NotificationsUtils {
    public static final String LED = "led";
    public static final String DHT_22 = "dht22";
    public static final String LED_ID = "id";
    public static final int LED_ID_VALUE = 2;
    public static final String STATUS = "status";
    public static final String ON = "on";
    public static final String OFF = "off";

    private final TimestampService timestampService;
    private final WebClient webClient;

    /**
     * Make call using WebClient with config reactor netty HTTP client.
     * By default read and connect timeout of 2s, to notify client side.
     *
     * @param ui ui
     * @param apexCharts line char
     * @param service chart service
     * @param h2Temperature h2 for humidity
     * @param h2Humidity h2 for temperature
     * @param sensorDht22GridServices grid for display data of sensor
     */
    public void readDht22Sensor(final UI ui, final ApexCharts apexCharts,
                                final ApexChartService service,
                                final H2 h2Temperature, final H2 h2Humidity,
                                final SensorDht22GridServices sensorDht22GridServices) {

        final Flux<SensorDht22Mapper> sensorResponseMapper = this.webClient
                .get()
                .uri(DHT_22)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SensorDht22Mapper.class)
                .delaySubscription(Duration.ofSeconds(2))//1
                .repeat()
                .doOnError((Throwable throwable) -> {
                    log.warn("Error {}", throwable);
                    ui.access(() -> this.showError(throwable.getMessage()));
                })
                .retryWhen(Retry.indefinitely());

        this.subscribeSensor(sensorResponseMapper, ui, apexCharts, service, h2Temperature,
                h2Humidity, sensorDht22GridServices);
    }

    /**
     * Make Subscription inside
     *
     * @param publisher               the Flux response
     * @param ui                      the ui
     * @param apexCharts              line char
     * @param service                 chart service
     * @param h2Temperature           h2 for humidity
     * @param h2Humidity              h2 for temperature
     * @param sensorDht22GridServices grid for display data of sensor
     */
    private void subscribeSensor(final Flux<SensorDht22Mapper> publisher,
                                 final UI ui, final ApexCharts apexCharts,
                                 final ApexChartService service,
                                 final H2 h2Temperature, final H2 h2Humidity,
                                 final SensorDht22GridServices sensorDht22GridServices) {

        publisher
                .subscribe((SensorDht22Mapper sensorDht22Mapper) -> {
                    ui.access(() -> {
                        //Filter SensorDht22Mapper response to model SensorDht22
                        final Optional<SensorDht22> optionalSensor = this.mappingResponse(sensorDht22Mapper);
                        //Only values greater than 0
                        if (optionalSensor.isPresent()) {
                            SensorDht22 sensorDht22 = optionalSensor.get();
                            sensorDht22GridServices.setData(sensorDht22);

                            Series<Object> seriesHumidities = this.humidity(sensorDht22, apexCharts, service, h2Humidity);
                            Series<Object> seriesTemperatures = this.temperature(sensorDht22, apexCharts, service, h2Temperature);

                            //Aqui pintamos nuestra chart con dos series.
                            apexCharts.updateSeries(seriesHumidities, seriesTemperatures);
                        }
                    });
                });
    }

    /**
     * Display humidity in chart and h2
     *
     * @param sensorDht22 the sensorDht22
     * @param apexCharts  line char
     * @param service     chart service
     * @param h2Humidity  the humidity
     * @return Series<Object> of humidity
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
     * Display temperature in chart and h2
     *
     * @param sensorDht22   the sensorDht22
     * @param apexCharts    line char
     * @param service       chart service
     * @param h2Temperature the temperature
     * @return Series<Object> of temperature
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

    /**
     * The valid model SensorDht22 from sensorDht22Mapper
     *
     * @param sensorDht22Mapper from response
     * @return Optional of {@link SensorDht22}
     */
    private Optional<SensorDht22> mappingResponse(final SensorDht22Mapper sensorDht22Mapper) {
        SensorDht22.SensorDht22Builder builder = SensorDht22.builder();
        //validate humidity and temperature up to 0 not, -999
        if (sensorDht22Mapper.getHumidity() > 0 && sensorDht22Mapper.getHumidity() <= 100) {
            builder.humidity(sensorDht22Mapper.getHumidity());
        }
        if (sensorDht22Mapper.getTemperature() > 0) {
            builder.temperature(sensorDht22Mapper.getTemperature());
        }
        //create SensorDht22 from builder
        SensorDht22 sensorDht22 = builder
                .id(sensorDht22Mapper.getId())
                .sensor(sensorDht22Mapper.getSensor())
                .type(sensorDht22Mapper.getType())
                .status(sensorDht22Mapper.getStatus())
                .build();

        if (sensorDht22.getTemperature() == 0.0 && sensorDht22.getHumidity() == 0.0) {
            return Optional.empty();
        }
        return Optional.of(sensorDht22);
    }

}
