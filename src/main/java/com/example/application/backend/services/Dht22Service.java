package com.example.application.backend.services;

import com.example.application.backend.model.SensorDht22;
import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.config.builder.YAxisBuilder;
import com.github.appreciated.apexcharts.config.xaxis.Labels;
import com.github.appreciated.apexcharts.config.xaxis.labels.DatetimeFormatter;
import com.github.appreciated.apexcharts.config.yaxis.Title;
import com.github.appreciated.apexcharts.config.yaxis.title.Style;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author rubn
 */
@Log4j2
@Service
public class Dht22Service {

    public static final String BASE_URL = "http://192.168.1.128:8081/async-esp8266/api/v1/";
    public static final String LED = "led";
    public static final String DHT_22 = "dht22";
    public static final String LED_ID = "id";
    public static final int LED_ID_VALUE = 2;
    public static final String STATUS = "status";
    public static final String ON = "on";
    public static final String OFF = "off";

    private HourService hourService;

    @Autowired
    public Dht22Service(final HourService hourService) {
        this.hourService = hourService;
    }

    public void readDht22Sensor(final UI ui, final ApexCharts lineChart) {
        lineChart.setDebug(true);
        final List<Double> datasHumidities = new CopyOnWriteArrayList<>();
        datasHumidities.add(0.0);
        final List<Double> datasTemps = new CopyOnWriteArrayList<>();
        datasTemps.add(0.0);
        final List<String> dataHours = new CopyOnWriteArrayList<>();
        dataHours.add("");

        WebClient.create(BASE_URL)
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
                        final Double humidity = Double.valueOf(sensorDht22.getHumidity());
                        final Double temperature = Double.valueOf(sensorDht22.getTemperature());
                        datasHumidities.add(humidity);
                        datasTemps.add(temperature);
                        log.info("SensorDht22 humidity: {}", humidity);
                        log.info("SensorDht22 temperature: {}", temperature);
                        lineChart.updateSeries(
                                new Series<>("Humidity % ", datasHumidities.toArray()),
                                new Series<>("Temperatures ª ", datasTemps.toArray()));
                    });
                });
    }

    private static String getFormattedString() {
        ZonedDateTime zdt = Instant.ofEpochMilli(Instant.now().toEpochMilli()).atZone(ZoneId.systemDefault());
        LocalDateTime ldt = LocalDateTime.of(zdt.getYear(),
                zdt.getMonth(),
                zdt.getDayOfMonth(),
                zdt.getHour(),
                zdt.getMinute(),
                zdt.getSecond(),
                0);
        return ldt.toString();
    }

}
