package com.example.application.backend.services.charts;

import com.example.application.backend.model.VaadinServerTimestamp;
import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.animations.builder.DynamicAnimationBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.AnimationsBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.ToolbarBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.grid.builder.RowBuilder;
import com.github.appreciated.apexcharts.config.legend.HorizontalAlign;
import com.github.appreciated.apexcharts.config.legend.Position;
import com.github.appreciated.apexcharts.config.series.SeriesType;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.stroke.LineCap;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.config.xaxis.XAxisType;
import com.github.appreciated.apexcharts.config.xaxis.builder.LabelsBuilder;
import com.github.appreciated.apexcharts.config.xaxis.builder.TitleBuilder;
import com.github.appreciated.apexcharts.helper.Coordinate;
import com.github.appreciated.apexcharts.helper.Series;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * @autor Detlef Boehm
 * @author rubn
 */
@Log4j2
@Service
public class ApexChartService {

    public static final int MAX_VALUES = 11;
    private static final String FONT_SIZE = "16px";
    public static final String HUMIDITY = "Humidity%";
    public static final String TEMPERATURE = "TemperatureºC";
    public static final String TITLE_YAXYS = HUMIDITY + " - " + TEMPERATURE;
    public static final String COLOR_RED = "#C60000FF";
    public static final String COLOR_BLUE = "#000098FF";
    public static final String COLOR_YELLOW = "#FFD500FF";

    private final String FORMATTER_TIMESTAMP = "function (value, timestamp) {\n" +
            "  var date = new Date(timestamp);" +
            "  var hours = date.getHours();\n" +
            "  var minutes = date.getMinutes();\n" +
            "  var seconds = date.getSeconds();" +
            "  var ampm = hours >= 12 ? 'pm' : 'am';\n" +
            "  hours = hours % 12;\n" +
            "  hours = hours ? hours : 12; // the hour '0' should be '12'\n" +
            "  minutes = minutes < 10 ? '0'+minutes : minutes;\n" +
            "  return hours + ':' + minutes + ':' + ('0'+seconds).slice(-2) + ' ' + ampm;\n" +
            "}";


    public ApexCharts getLineChart() {
        return ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.line)
                        .withToolbar(ToolbarBuilder.get()
                                .withShow(false)
                                .build())
                        .withZoom(ZoomBuilder.get()
                                .withEnabled(false)
                                .build())
                        .withAnimations(AnimationsBuilder.get()
                                .withDynamicAnimation(DynamicAnimationBuilder.get()
                                        .withSpeed(500)
                                        .withEnabled(true)
                                        .build())
//                                .withAnimateGradually(AnimateGraduallyBuilder.get()
//                                        .withDelay(2000)
//                                        .withEnabled(true)
//                                        .build())
//                                .withEasing(Easing.linear)
//                                .withSpeed(2000.0)
//                                .withEnabled(true)
//                                .build())
                        .build())
                        .build())
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false)
                        .build())
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("DHT22 - AM2302")
                        .withAlign(Align.center)
                        .build())
                .withStroke(StrokeBuilder.get()
                        .withLineCap(LineCap.butt)
                        .withCurve(Curve.straight)
                        .build())
                .withLegend(LegendBuilder.get()
                        .withPosition(Position.top)
                        .withHorizontalAlign(HorizontalAlign.right)
                        .withShowForSingleSeries(true)
                        .withShow(true)
                        .build())
                .withYaxis(YAxisBuilder.get()
                        .withOpposite(false)
//                        .withTitle(com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder.get()
//                                .withText(titleText)
//                                .withStyle(StyleBuilder.get()
//                                        //.withFontSize(FONT_SIZE)
//                                        .build())
//                                .build())
                        .build())
                .withXaxis(XAxisBuilder.get()
                        .withType(XAxisType.datetime)
                        .withLabels(LabelsBuilder.get()
                                .withFormatter(FORMATTER_TIMESTAMP)
                                .withShowDuplicates(false)
                                //.withRotateAlways(true)
                                .withShow(true)
                                .build())
                        .withAxisTicks(com.github.appreciated.apexcharts.config.xaxis.builder.AxisTicksBuilder.get()
                                .withHeight(5.0)
                                .withColor("#000000")
                                .withShow(true)
                                .build())
                        .withTitle(TitleBuilder.get()
                                .withText("Time")
                                .withStyle(com.github.appreciated.apexcharts.config.xaxis.title.builder.StyleBuilder.get()
                                    .withFontSize(FONT_SIZE)
                                    .build())
                                .build())
                        .withTickAmount(BigDecimal.valueOf(10))
                        .build())
                .withGrid(GridBuilder.get()
                        .withRow(RowBuilder.get()
                                .withColors("#f3f3f3", "transparent")
                                .withOpacity(0.8)
                                .build())
                        .build())
                .withSeries(new Series<>(HUMIDITY, SeriesType.line, 0.0),
                        new Series<>(TEMPERATURE, SeriesType.line,0.0))
                .build();
    }

    public Series<Object> getApexChartsCoordinateSeries(String name, List<VaadinServerTimestamp> timestamp) {
        final var apexChartsData = getApexChartsSeries(timestamp);
        final var apexChartsLabels = getApexChartsLabels(timestamp);
        final var coordinates = new Coordinate[apexChartsData.length - 1];
        final var coordinateSeries = new Series<>(name);
        IntStream.range(0, apexChartsLabels.length - 1 )
                .forEach(i -> {
                    try {
                        coordinates[i] = new Coordinate<Object, Object>(apexChartsLabels[i], apexChartsData[i]);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        log.info(ex.getMessage());
                    }
                });
        coordinateSeries.setData(coordinates);
        return coordinateSeries;
    }

    public Double[] getApexChartsSeries(List<VaadinServerTimestamp> timestamp) {
        var values = new CopyOnWriteArrayList<>();
        final int deltaPos = MAX_VALUES - timestamp.size();
        if (deltaPos > 0) {
            //take the first initial value from the input list
            final VaadinServerTimestamp bean = timestamp.get(0);
            IntStream.range(0, deltaPos).forEach(index -> values.add(bean.getValue()));
        }
        timestamp.forEach(value -> values.add(value.getValue()));
        return values.toArray(new Double[]{});
    }

    public String[] getApexChartsLabels(List<VaadinServerTimestamp> timestamp) {
        var values = new CopyOnWriteArrayList<>();
        int deltaPos = MAX_VALUES - timestamp.size();
        if (deltaPos > 0) {
            // take the first initial values and average the timeStamp difference and extrapolate to the past
            long timeStampDiff = this.averageTimestampDiffs(timestamp);
            var bean = timestamp.get(0);
            // and fill the output list from index 0 to deltaPos with extrapolated timestamp
            IntStream.range(0, deltaPos).forEach(index -> {
                values.add(0, getISOString(bean.getTimestamp() - (timeStampDiff * index)));
            });
        } // else go on
        timestamp.forEach(bean -> values.add(getISOString(bean.getTimestamp())));
        return values.toArray(new String[]{});
    }

    /**
     * @param timestampValues
     * @return long
     */
    private long averageTimestampDiffs(List<VaadinServerTimestamp> timestampValues) {
        List<Long> timestampList = timestampValues
                .stream()
                .map(VaadinServerTimestamp::getTimestamp)
                .collect(Collectors.toList());

        return Math.round(LongStream.range(0, timestampList.size() - 1)
                .map(i -> (int) (timestampList.get((int) (i + 1)) - timestampList.get((int) i)))
                .summaryStatistics()
                .getAverage());
    }

    private String getISOString(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String[] getRandomTriColor() {
        final String[] colors = new String[]{COLOR_RED, COLOR_BLUE, COLOR_YELLOW};
        Collections.shuffle(Arrays.asList(colors));
        return colors;
    }

}
