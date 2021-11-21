package com.example.application.views.blinkled;

import com.example.application.backend.services.SwitchLedService;
import com.example.application.views.main.MainView;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.util.UriBuilder;

import javax.annotation.PostConstruct;

import java.net.URI;
import java.util.function.Function;

import static com.example.application.backend.services.Dht22Service.LED;
import static com.example.application.backend.services.Dht22Service.LED_ID;
import static com.example.application.backend.services.Dht22Service.LED_ID_VALUE;
import static com.example.application.backend.services.Dht22Service.OFF;
import static com.example.application.backend.services.Dht22Service.ON;
import static com.example.application.backend.services.Dht22Service.STATUS;

/**
 * @author rubn
 */
@Log4j2
@SpringComponent
@UIScope
@Route(value = "blinking-led", layout = MainView.class)
@RequiredArgsConstructor
public class SwitchLedView extends VerticalLayout {
    private final SwitchLedService ledPinService;
    private final ToggleButton ledPinButton = new ToggleButton("On/Off");

    @PostConstruct
    public void init() {
        super.add(this.ledPinButton);
    }

    private void ledPinService(final UI ui) {
        this.ledPinButton.addValueChangeListener(e -> {
            if (e.getValue()) {
                this.ledPinService.highLowPin(ui,
                        (Function<UriBuilder, URI>) uriBuilder -> uriBuilder.path(LED)
                                .queryParam(LED_ID, LED_ID_VALUE)
                                .queryParam(STATUS, ON)
                                .build());
            } else {
                this.ledPinService.highLowPin(ui,
                        (Function<UriBuilder, URI>) uriBuilder ->
                                uriBuilder.path(LED)
                                        .queryParam(LED_ID, LED_ID_VALUE)
                                        .queryParam(STATUS, OFF)
                                        .build());
            }
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()) {
            //Service led pin 2
            this.ledPinService(attachEvent.getUI());
        }
    }
}
