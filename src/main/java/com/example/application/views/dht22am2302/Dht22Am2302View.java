package com.example.application.views.dht22am2302;

import com.example.application.backend.services.charts.ApexChartService;
import com.example.application.backend.services.Dht22Service;
import com.example.application.backend.services.LedPinService;
import com.example.application.backend.services.grid.SensorDht22GridServices;
import com.example.application.util.ResponsiveHeaderDiv;
import com.example.application.views.main.MainView;
import com.github.appreciated.apexcharts.ApexCharts;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static com.example.application.backend.services.Dht22Service.*;

/**
 * Show humidities and temperatures
 */
@SpringComponent
@UIScope
@Log4j2
@Route(value = "dht22-am2302", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("DHT-22-AM2302")
public class Dht22Am2302View extends VerticalLayout implements ResponsiveHeaderDiv {

    public static final String BOX_SHADOW_PROPERTY = "box-shadow";
    public static final String BOX_SHADOW_VALUE = "0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)";

    private final ToggleButton ledPinButton = new ToggleButton("On/Off");
    private ApexCharts apexCharts;
    private ApexChartService apexChartService;
    private LedPinService ledPinService;
    private Dht22Service dht22Service;
    private H2 h2Temperature = new H2("0");
    private H2 h2Humidity = new H2("0");
    private SensorDht22GridServices sensorDht22GridServices;
    private String[] colors;

    @Autowired
    public Dht22Am2302View(final ApexChartService apexChartService,
                           final LedPinService ledPinService,
                           final Dht22Service dht22Service,
                           final SensorDht22GridServices sensorDht22GridServices) {

        this.sensorDht22GridServices = sensorDht22GridServices;
        this.ledPinService = ledPinService;
        this.apexChartService = apexChartService;
        this.dht22Service = dht22Service;
        super.setSizeFull();

        super.add(this.headerDiv());
        super.add(this.gridAndChart());
    }

    private Div headerDiv() {
        //Add logo sensor dht22
        final Image image = new Image("/images/dht22.jpg","");
        image.setWidth("164px");
        image.setHeight("94px");
        final Div divImage = this.createDiv(image,"Dht22 Am2302");
        divImage.add(new Hr());
        final Div divTemperature = this.createDiv(h2Temperature,"Temperature ºC");
        divTemperature.add(new Hr());
        final Div divHumidity = this.createDiv(h2Humidity,"Humidity %");
        divHumidity.add(new Hr());

        return this.createHeaderDivWithSpaceAround(divImage, divTemperature, divHumidity);
    }

    private HorizontalLayout gridAndChart() {
        this.apexCharts = apexChartService.getLineChart();
        this.colors = apexChartService.getRandomTriColor();
        apexCharts.setColors(colors);
        final Div gridSensorData = new Div(this.sensorDht22GridServices);
        final Div chart = new Div(this.apexCharts);
        Stream.of(gridSensorData, chart).forEach(e -> {
            e.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);
            e.setWidthFull();
        });
        final HorizontalLayout horizontalLayout = new HorizontalLayout(gridSensorData, chart);
        horizontalLayout.setWidthFull();
        horizontalLayout.addClassName("chart-grid");

        return horizontalLayout;
    }

    private void ledPinService(final UI ui) {
        this.ledPinButton.addValueChangeListener(e -> {
            if (e.getValue() == Boolean.TRUE) {
                this.ledPinService.highLowPin(ui,
                        uriBuilder -> uriBuilder.path(LED)
                                .queryParam(LED_ID, LED_ID_VALUE)
                                .queryParam(STATUS, ON)
                                .build());
            } else {
                this.ledPinService.highLowPin(ui,
                        uriBuilder -> uriBuilder.path(LED)
                                .queryParam(LED_ID, LED_ID_VALUE)
                                .queryParam(STATUS, OFF)
                                .build());
            }
        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            //Service led pin 2
            this.ledPinService(attachEvent.getUI());
            this.dht22Service.readDht22Sensor(attachEvent.getUI(),apexCharts,
                    apexChartService, h2Temperature, h2Humidity, sensorDht22GridServices);
        }
    }

}
