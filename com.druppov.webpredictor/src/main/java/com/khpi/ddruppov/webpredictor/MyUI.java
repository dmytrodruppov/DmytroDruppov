package com.khpi.ddruppov.webpredictor;

import com.khpi.ddruppov.domain.EDataType;
import com.khpi.ddruppov.domain.ForecastData;
import com.khpi.ddruppov.domain.HistoricalData;
import com.khpi.ddruppov.domain.PredictedResult;
import com.khpi.ddruppov.domain.TrainingModel;
import com.khpi.ddruppov.repository.ForecastDataRepository;
import com.khpi.ddruppov.repository.HistoryDataRepository;
import com.khpi.ddruppov.repository.TrainingModelRepository;
import com.khpi.ddruppov.rest.IPredictorRest;
import com.khpi.ddruppov.util.QualityMetricsUtil;
import com.khpi.ddruppov.webpredictor.ui.CreateModelWindow;
import com.khpi.ddruppov.webpredictor.ui.PredictForecastWindow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisTitle;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataLabels;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.PlotOptionsLine;
import com.vaadin.addon.charts.model.Shape;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.provider.Query;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.annotation.WebServlet;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@SpringUI
public class MyUI extends UI {

    private static final Logger LOGGER = Logger.getLogger(MyUI.class.toString());

    private Label modelNameLabel = new Label();
    private ComboBox<String> modelNameCbox = new ComboBox<>("Training model");
    private VerticalLayout layout;
    private Button createModelBttn = new Button("Create model");
    private Button drawChartBttn = new Button("View");
    private Button predictBttn = new Button("Predict");
    private Grid<PredictedResult> resultsGrid = new Grid<>(PredictedResult.class);
    private Button saveResults = new Button("Save");
    private ComboBox<String> historyDataComboBox = new ComboBox<>();
    private Button addHistoryDataToChart = new Button("Add historical");
    private List<HistoricalData> historicalDataList;
    private Layout drawLayout;
    private TrainingModel selectedTrainingModel;
    private HistoricalData selectedHistoricalData;
    private Grid<PredictedResult> historyDataGrid = new Grid<>(PredictedResult.class);
    private HorizontalLayout gridsLayout = new HorizontalLayout();
    private Button addHisoricalBttn = new Button("Add historical data");

    private Label mseLabel = new Label();
    private Label relativeError = new Label();
    private Label absoluteError = new Label();

    @Autowired
    private ForecastDataRepository forecastDataRepository;

    @Autowired
    private HistoryDataRepository historyDataRepository;

    @Autowired
    private TrainingModelRepository modelRepository;

    @Autowired
    private IPredictorRest rest;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        modelNameLabel.setValue("Please choose you model ");

        saveResults.addClickListener(clickEvent -> {
            setSaveResults();
        });

        gridsLayout.setCaption("Forecast data");
        //historyDataGrid.setCaption("Historical data");

        if (selectedTrainingModel != null) {
            historicalDataList = historyDataRepository.findByType(selectedTrainingModel.getDataType());
            LOGGER.info("found historical data, size: " + historicalDataList.size());
            historyDataComboBox.setItems(historicalDataList.stream().map(HistoricalData::getId));
        }
        modelNameCbox.setEmptySelectionAllowed(false);
        //historyDataComboBox.setEmptySelectionAllowed(false);
        //historyDataComboBox.getDataProvider().refreshAll();

        historyDataComboBox.addValueChangeListener(valueChangeEvent -> {
            if (historyDataComboBox.getValue() != null) {
                selectedHistoricalData = historyDataRepository.findOne(historyDataComboBox.getValue());
            } else {
                selectedHistoricalData = null;
            }
        });

//        addHistoryDataToChart.addClickListener(clickEvent -> {
//            drawLayout = drawChart(selectedHistoricalData);
//        });

        drawChartBttn.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                Window window = new Window();
                window.setWidth("500px");
                window.setHeight("500px");
                window.setModal(true);

                Layout mainLayout = new FormLayout();
                if (historyDataComboBox.getValue() == null) {
                    drawLayout = drawChart(null);
                } else {
                    drawLayout = drawChart(selectedHistoricalData);
                }
                mainLayout.addComponent(drawLayout);
                mainLayout.addComponent(mseLabel);
                mainLayout.addComponent(relativeError);
                mainLayout.addComponent(absoluteError);
                mainLayout.addComponent(saveResults);
                window.setContent(mainLayout);

                addWindow(window);
            }
        });
        ;
        modelNameCbox.addValueChangeListener(valueChangeEvent -> {
            modelNameLabel.setValue("Model '" + modelNameCbox.getValue() + "' is selected");
            predictBttn.setEnabled(true);
            selectedTrainingModel = modelRepository.findByName(modelNameCbox.getValue());

            //TODO: rewrite code duplication
            historicalDataList = historyDataRepository.findByType(selectedTrainingModel.getDataType());
            LOGGER.info("found historical data, size: " + historicalDataList.size());
            if (historicalDataList != null && !historicalDataList.isEmpty()) {
                historyDataComboBox.setItems(historicalDataList.stream().map(HistoricalData::getId));
            } else {
                //LOGGER.info("clear items");
                historyDataComboBox.clear();
            }
            historyDataComboBox.getDataProvider().refreshAll();
            resetForecastData();
        });

        addHisoricalBttn.addClickListener(clickEvent -> {
            if (gridsLayout.getComponentCount() > 1) {
                gridsLayout.removeComponent(historyDataGrid);
            } else {
                List<PredictedResult> predictedResults = selectedHistoricalData.getValues();
                historyDataGrid.setItems(predictedResults);
                gridsLayout.addComponent(historyDataGrid);
            }
        });

        predictBttn.setEnabled(false);
        predictBttn.addClickListener(clickEvent -> {
            TrainingModel selectedModel = modelRepository.findByName(modelNameCbox.getValue());
            PredictForecastWindow window = new PredictForecastWindow(rest, selectedModel, resultsGrid);
            addWindow(window);
        });

        createModelBttn.addClickListener(clickEvent -> {
            CreateModelWindow window = new CreateModelWindow(rest, modelRepository, modelNameCbox);
            addWindow(window);
        });

        modelNameCbox.setItems(rest.getTrainModels());

        drawChartBttn.setEnabled(false);
        addHistoryDataToChart.setEnabled(false);
        addHisoricalBttn.setEnabled(false);
        historyDataComboBox.setEnabled(false);

        resultsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        historyDataGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        resultsGrid.addSelectionListener(event -> {
            long count = resultsGrid.getDataProvider().fetch(new Query<>()).count();
            if (count > 0) {
                drawChartBttn.setEnabled(true);
                addHistoryDataToChart.setEnabled(true);
                historyDataComboBox.setEnabled(true);
                addHisoricalBttn.setEnabled(true);
            } else {
                drawChartBttn.setEnabled(false);
                addHistoryDataToChart.setEnabled(false);
                historyDataComboBox.setEnabled(false);
                addHisoricalBttn.setEnabled(false);
            }
        });
        resultsGrid.getColumn("date").setHidden(true);
        historyDataGrid.getColumn("date").setHidden(true);

        Layout predictionButtonLayout = new HorizontalLayout(createModelBttn, predictBttn);
        layout.addComponent(modelNameLabel);
        layout.addComponent(modelNameCbox);
        layout.addComponent(predictionButtonLayout);
        gridsLayout.addComponent(resultsGrid);
        layout.addComponent(gridsLayout);
        layout.addComponents((new HorizontalLayout(drawChartBttn, historyDataComboBox, addHisoricalBttn)));

//        layout.setComponentAlignment(modelNameLabel, Alignment.TOP_CENTER);
//        layout.setComponentAlignment(modelNameCbox, Alignment.TOP_CENTER);
//        layout.setComponentAlignment(predictionButtonLayout, Alignment.TOP_CENTER);
        //layout.setComponentAlignment(resultsGrid, Alignment.TOP_CENTER);
        //layout.setComponentAlignment(drawChartBttn, Alignment.TOP_CENTER);

        setContent(layout);
    }

    private void resetForecastData() {
        resultsGrid.setItems(Collections.emptyList());
        resultsGrid.getDataProvider().refreshAll();
        drawChartBttn.setEnabled(false);
        addHistoryDataToChart.setEnabled(false);
        historyDataComboBox.setEnabled(false);
    }

    private void setSaveResults() {
        List<PredictedResult> results =
            resultsGrid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());

        ForecastData data = new ForecastData();
        data.setResult(results);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        data.setDate(new Date(calendar.getTimeInMillis()));
        data.setId(String.valueOf(calendar.getTimeInMillis()) + data.getDate());

        LOGGER.info("try to insert forecast data, size: " + results.size());
        forecastDataRepository.save(data);
        List<ForecastData> foundData = forecastDataRepository.findAll();
        LOGGER.info("found data, size: " + foundData.size());

    }


    private VerticalLayout drawChart(HistoricalData historicalData) {
        final VerticalLayout layout = new VerticalLayout();
        Chart chart = new Chart();

        Configuration configuration = chart.getConfiguration();
        configuration.getChart().setType(ChartType.LINE);

        String title = "";
        if (EDataType.PRICE_DAILY.equals(selectedTrainingModel.getDataType())) {
            title = "Price of the electricity power per day";
        } else {
            title = "Electricity consumption per month";
        }
        configuration.getTitle()
            .setText(title);

        Stream<PredictedResult> streamEntities = resultsGrid.getDataProvider().fetch(new Query<>());

        List<Double> values = new ArrayList<>();
        List<String> time = new ArrayList<>();
        streamEntities.forEach(item -> {
            values.add(item.getPrediction().doubleValue());
            time.add(item.getTime());
        });

        if (EDataType.PRICE_DAILY.equals(selectedTrainingModel.getDataType())) {
            configuration.getxAxis().setTitle("Hours");
        } else {
            configuration.getxAxis().setTitle("Days");
        }
        configuration.getxAxis().setCategories(time.toArray(new String[time.size()]));

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle(new AxisTitle("Wh-market price (USD)"));
//        configuration
//            .getTooltip()
//            .setFormatter(
//                "'<b>'+ this.series.name +'</b><br/>'+this.x +': '+ this.y +'°C'");

        PlotOptionsLine plotOptions = new PlotOptionsLine();
        plotOptions.setEnableMouseTracking(false);
        configuration.setPlotOptions(plotOptions);

        DataSeries ds = new DataSeries();
        ds.setName("Prediction");

        ds.setData(values.toArray(new Double[values.size()]));
        DataLabels callout = new DataLabels(true);
        callout.setShape(Shape.CALLOUT);
        callout.setY(-12);
        ds.get(6).setDataLabels(callout);
        configuration.addSeries(ds);

        if (historicalData != null) {
            ds = new DataSeries();
            ds.setName("Historical data");
            List<PredictedResult> historical = historicalData.getValues();
            ds.setData(historical.stream().map(PredictedResult::getPrediction).toArray(Double[]::new));
            ds.get(6).setDataLabels(callout);
            configuration.addSeries(ds);

            double relative = QualityMetricsUtil.calculateAbsoluteRelativeError(
                values, historical.stream().map(PredictedResult::getPrediction).collect(Collectors.toList()));
            double absolute = QualityMetricsUtil.calculateAverageRelativeError(
                values, historical.stream().map(PredictedResult::getPrediction).collect(Collectors.toList()));
            double mse = QualityMetricsUtil.calculateMSE(
                values, historical.stream().map(PredictedResult::getPrediction).collect(Collectors.toList()));
            LOGGER.info("Relative error : " + relative);
            LOGGER.info("Absolute error: " + absolute);
            LOGGER.info("MSE: " + mse);

            relativeError.setValue("Relative error : " + relative);
            absoluteError.setValue("Absolute error: " + absolute);
            mseLabel.setValue("MSE: " + mse);
        }

        layout.addComponent(chart);
        return layout;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
