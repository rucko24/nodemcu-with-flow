package com.example.application.backend.charts;

import com.example.application.backend.services.HourService;
import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.animations.Easing;
import com.github.appreciated.apexcharts.config.chart.animations.builder.DynamicAnimationBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.AnimationsBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.ToolbarBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.grid.builder.RowBuilder;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.config.xaxis.Labels;
import com.github.appreciated.apexcharts.config.xaxis.XAxisType;
import com.github.appreciated.apexcharts.config.yaxis.Title;
import com.github.appreciated.apexcharts.config.yaxis.title.Style;
import com.github.appreciated.apexcharts.helper.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 *
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LineChartService {

    private static final String HOUR_FORMAT = "dd/MM/yy HH:mm";
    //private static final String HOUR_FORMAT = "HH:mm:ss";

    @Autowired
    private HourService hourService;

    /**
     *
     * @return ApexCharts
     */
    public ApexCharts getLineChart() {
        Labels labels = new Labels();
        labels.setFormat("dd/MM/yy HH:mm:ss");
        Title title = new Title();
        title.setText("Humidity % - Temperaturesª");
        final Style style = new Style();
        style.setFontSize("16");
        title.setStyle(style);
        return ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.line)
                        .withAnimations(AnimationsBuilder.get()
                                .withEnabled(true)
                                .withEasing(Easing.linear)
                                .withDynamicAnimation(DynamicAnimationBuilder.get()
                                        .withSpeed(2000)
                                        .build())
                                .build())
                        .withToolbar(ToolbarBuilder.get()
                                .withShow(false)
                                .build())
                        .withZoom(ZoomBuilder.get()
                                .withEnabled(false)
                                .build())
                        .build())
                .withStroke(StrokeBuilder.get()
                        .withCurve(Curve.straight)
                        .build())
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false)
                        .build())
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("DHT-22-AM2302")
                        .withAlign(Align.center)
                        .build())
                .withGrid(GridBuilder.get()
                        .withRow(RowBuilder.get()
                                .withColors("#f3f3f3", "transparent")
                                .withOpacity(0.8)
                                .build())
                        .build())
                .withYaxis(YAxisBuilder.get()
                        .withOpposite(false)
                        .withTitle(title)
                        .withShowAlways(false)
                        .build())
                .withXaxis(XAxisBuilder.get()
                        .withType(XAxisType.numeric)
                        //.withTitle(Ti)
                        //.withCategories(dataHours)
                        .withTickAmount(BigDecimal.valueOf(7))
                        .withLabels(labels)
                        .build())
                .withSeries(new Series<>("humidities",0),
                        new Series<>("temperatures", 0))
                .build();
    }

}
