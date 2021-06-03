package com.example.application.views.dht22am2302;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "dht22-am2302", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("DHT-22-AM2302")
public class DHT22AM2302View extends HorizontalLayout {

    private TextField name;
    private Button sayHello;

    public DHT22AM2302View() {
        addClassName("d-h-t22-a-m2302-view");
        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        add(name, sayHello);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
    }

}
