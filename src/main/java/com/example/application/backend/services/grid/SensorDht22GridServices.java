package com.example.application.backend.services.grid;

import com.example.application.backend.model.SensorDht22;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author rubn
 */
@Getter
@Log4j2
@SpringComponent
@UIScope
public class SensorDht22GridServices extends Grid<SensorDht22> {

    private final List<SensorDht22> sensorDht22List = new CopyOnWriteArrayList<>();
    private final List<Integer> errorsHumidity = new CopyOnWriteArrayList<>();
    private final List<Integer> errorsTemperature = new CopyOnWriteArrayList<>();
    private static final String ERROR = "error";

    @Autowired
    public SensorDht22GridServices() {
        super.setPageSize(11);
        super.setWidthFull();
        super.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        super.addColumn(SensorDht22::getType).setHeader("Sensor").setWidth("20px");

        final String templateHumidity = "<Span class='label-current' status$='[[item.error]]'> [[item.current]] </Span>";
        super.addColumn(TemplateRenderer.<SensorDht22>of(templateHumidity)
                .withProperty("current", SensorDht22::getHumidity)
                .withProperty(ERROR, s -> s.getHumidity() > 100 ? ERROR : ""))
                .setHeader("Humidity %");

        final String templateTemperature = "<Span class='label-current' status$='[[item.error2]]'> [[item.current2]] </Span>";
        super.addColumn(TemplateRenderer.<SensorDht22>of(templateTemperature)
                .withProperty("current2", SensorDht22::getTemperature)
                .withProperty("error2", s -> s.getTemperature() < 500 ? ERROR : ""))
                .setHeader("Temperature ºC");

        super.getColumns().forEach(e -> {
            e.setResizable(true);
            e.setComparator(Comparator.comparing(Objects::toString));
            e.setTextAlign(ColumnTextAlign.CENTER);
        });

    }

    public void setData(SensorDht22 sensorDht22) {
        this.sensorDht22List.add(sensorDht22);
        super.setDataProvider(DataProvider.fromCallbacks(
                query -> {
                    int offset = query.getOffset();
                    return sensorDht22List.subList(offset,query.getOffset() + query.getLimit()).stream();
                }, query -> sensorDht22List.size()));
    }

}
