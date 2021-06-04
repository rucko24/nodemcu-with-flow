package com.example.application.views.dht22am2302;

import com.example.application.backend.charts.LineChartService;
import com.example.application.backend.model.SensorDht22;
import com.example.application.backend.services.HourService;
import com.example.application.backend.services.LedPinService;
import com.example.application.views.main.MainView;
import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

/**
 * Show humidities and temperatures
 */
@SpringComponent
@UIScope
@Log4j2
@Route(value = "dht22-am2302", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("DHT-22-AM2302")
public class DHT22AM2302View extends VerticalLayout {

    public static final String BASE_URL = "http://192.168.1.166:8081/async-esp8266/api/v1/";
    public static final String BOX_SHADOW_PROPERTY = "box-shadow";
    public static final String BOX_SHADOW_VALUE = "0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)";

    private final ToggleButton ledPinButton = new ToggleButton("On/Off");
    private ApexCharts lineChart;
    private LineChartService lineChartService;
    private HourService hour;
    private LedPinService ledPinService;

    @Autowired
    public DHT22AM2302View(final LineChartService lineChartService,
                           final HourService hour,
                           final LedPinService ledPinService) {
        this.ledPinService = ledPinService;
        this.hour = hour;
        this.lineChartService = lineChartService;
        addClassName("d-h-t22-a-m2302-view");

        super.add(this.ledPinButton);
        super.add(lineChartDiv());
    }

    private Div lineChartDiv() {
        this.lineChart = lineChartService.getLineChart();
        final Div divLineChart = new Div(lineChart);
        lineChart.setWidthFull();
        lineChart.setHeight("400px");
        divLineChart.setWidth("400px");
        divLineChart.getStyle().set("border","1px solid black");
        divLineChart.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);
        return divLineChart;
}

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(attachEvent.isInitialAttach()) {
            //Service led pin 2
            this.ledPinService(attachEvent.getUI());

            final List<Double> datasHumidities = new CopyOnWriteArrayList<>();
            datasHumidities.add(0.0);
            final List<Double> datasTemps = new CopyOnWriteArrayList<>();
            datasTemps.add(0.0);
            final List<String> dataHours = new CopyOnWriteArrayList<>();
            dataHours.add("");

            this.dht22Sensor(attachEvent.getUI(),datasHumidities, datasTemps);
        }
    }

    private void ledPinService(final UI ui) {
        this.ledPinButton.addValueChangeListener(e -> {
            if(e.getValue() == Boolean.TRUE) {
                this.ledPinService.highLowPin(ui,
                        uriBuilder -> uriBuilder.path("led")
                                .queryParam("id",2)
                                .queryParam("newstatus","on")
                                .build());
            } else {
                this.ledPinService.highLowPin(ui,
                        uriBuilder -> uriBuilder.path("led")
                                .queryParam("id",2)
                                .queryParam("newstatus","off")
                                .build());
            }
        });
    }

    private void dht22Sensor(final UI ui, final List<Double> datas, final List<Double> datasTemps) {
        WebClient.create(BASE_URL)
                .get()
                .uri("dht22")
                .accept(MediaType.APPLICATION_JSON)
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
                        final Double data = Double.valueOf(sensorDht22.getHumidity());
                        final Double dataTemp = Double.valueOf(sensorDht22.getTemperature());
                        datas.add(data);
                        datasTemps.add(dataTemp);
                        log.info("SensorDht22 humidity: {}", data );
                        log.info("SensorDht22 temperature: {}", dataTemp );
                        this.lineChart.updateSeries(
                                new Series<>("Humidity % ",datas.toArray()),
                                new Series<>("Temperatures ª ",datasTemps.toArray()));
                    });
                });

    }


}
