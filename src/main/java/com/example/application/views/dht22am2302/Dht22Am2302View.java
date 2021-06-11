package com.example.application.views.dht22am2302;

import com.example.application.backend.services.charts.ApexChartService;
import com.example.application.backend.services.Dht22Service;
import com.example.application.backend.services.LedPinService;
import com.example.application.views.main.MainView;
import com.github.appreciated.apexcharts.ApexCharts;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
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
import static com.example.application.backend.services.charts.ApexChartService.TITLE_YAXYS;

/**
 * Show humidities and temperatures
 */
@SpringComponent
@UIScope
@Log4j2
@Route(value = "dht22-am2302", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("DHT-22-AM2302")
public class Dht22Am2302View extends VerticalLayout {

    public static final String BOX_SHADOW_PROPERTY = "box-shadow";
    public static final String BOX_SHADOW_VALUE = "0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)";

    private final ToggleButton ledPinButton = new ToggleButton("On/Off");
    private ApexCharts apexCharts;
    private ApexChartService apexChartService;
    private LedPinService ledPinService;
    private Dht22Service dht22Service;

    @Autowired
    public Dht22Am2302View(final ApexChartService apexChartService,
                           final LedPinService ledPinService,
                           final Dht22Service dht22Service) {

        this.ledPinService = ledPinService;
        this.apexChartService = apexChartService;
        this.dht22Service = dht22Service;
        addClassName("d-h-t22-a-m2302-view");

        super.add(this.ledPinButton);
        super.add(lineChartDiv());
    }

    private Div lineChartDiv() {
        this.apexCharts = apexChartService.getLineChart(TITLE_YAXYS);
        apexCharts.setColors(apexChartService.getRandomTriColor());
        final Div divLineChart = new Div(apexCharts);
        Stream.of(apexCharts).forEach(HasSize::setWidthFull);
        apexCharts.setHeight("400px");
        divLineChart.getStyle().set("border", "1px solid black");
        divLineChart.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);
        return divLineChart;
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
            this.dht22Service.readDht22Sensor(attachEvent.getUI(),apexCharts, apexChartService);
        }
    }

}
