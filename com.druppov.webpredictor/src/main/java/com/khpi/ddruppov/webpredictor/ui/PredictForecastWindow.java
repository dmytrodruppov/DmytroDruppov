package com.khpi.ddruppov.webpredictor.ui;

import com.khpi.ddruppov.domain.PredictedResult;
import com.khpi.ddruppov.domain.TrainingModel;
import com.khpi.ddruppov.rest.IPredictorRest;
import com.khpi.ddruppov.rest.request.PredictForecastRequest;
import com.khpi.ddruppov.util.LibsvmValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PredictForecastWindow extends Window {

    private static final String COMPONENT_RELATIVE_WIDTH = "100%";
    private static final Logger LOGGER = Logger.getLogger(PredictForecastWindow.class.toString());
    private File uploadFile;
    private IPredictorRest predictorRest;
    private PredictForecastRequest predictForecastRequest;
    private OutputStream uploadStream;

    private Label uploadFileName = new Label("Need to upload prediction data");
    private Upload uploadFileBttn;
    private TextField modelName = new TextField("Model name");
    private FormLayout formLayout = new FormLayout();
    private Button confirmPredict = new Button("Make prediction");
    private Grid<PredictedResult> grid;
    private DateField dateField = new DateField("Forecast date");
    private ProgressBar uploadProgress;

    public PredictForecastWindow(IPredictorRest predictorRest, TrainingModel selectedModel, Grid<PredictedResult> grid) {
        this.predictorRest = predictorRest;
        this.grid = grid;
        setCaption("Predict by new data");
        setWidth("400px");
        setHeight("350px");
        setModal(true);
        setResizable(false);
        TrainFileReceiver receiver = new TrainFileReceiver();

        uploadFileBttn = new Upload("Upload train file", receiver);
        uploadFileBttn.addSucceededListener(this::uploadSucceeded);
        uploadFileBttn.addProgressListener(this::updateProgress);

        formLayout.setSizeFull();
        formLayout.setSpacing(true);
        formLayout.setMargin(true);
        formLayout.setHeight(COMPONENT_RELATIVE_WIDTH);
        formLayout.setWidth(COMPONENT_RELATIVE_WIDTH);
        modelName.setValue(selectedModel.getName());
        modelName.setReadOnly(true);

        uploadProgress = new ProgressBar(0);
        uploadProgress.setWidth(COMPONENT_RELATIVE_WIDTH);

        modelName.setRequiredIndicatorVisible(true);
        dateField.setRequiredIndicatorVisible(true);

        formLayout.addComponents(modelName, dateField, uploadFileBttn, uploadProgress,
                uploadFileName, confirmPredict);

        confirmPredict.addClickListener(clickEvent -> {

            if (!validate()) {
                return;
            }

            String path = uploadFile.getAbsolutePath();
            try {
                if (!LibsvmValidator.validatePredictionData(uploadFile.getAbsolutePath(), selectedModel.getSizeAttributes())) {
                    Notification.show("Data file is not valid for the model " + selectedModel.getName());
                    return;
                }
                predictForecastRequest = new PredictForecastRequest(selectedModel.getName(), path);

                List<PredictedResult> results = new ArrayList<>();
                List<Double> predictedValues = predictorRest.predict(predictForecastRequest);
                for (int i = 0; i < predictedValues.size(); i++) {
                    results.add(new PredictedResult(String.valueOf(i + 1), predictedValues.get(i).doubleValue()));
                }
                grid.setItems(results);
                grid.select(results.get(0));
                grid.deselectAll();
            } catch (IOException e) {
                e.printStackTrace();
            }

            close();
        });

        setContent(formLayout);
    }

    private boolean validate() {
        if (dateField.getValue() == null) {
            Notification.show("Please specify forecast date");
            return false;
        }
        if (uploadFile == null) {
            Notification.show("Please specify upload file");
            return false;
        }

        return true;
    }

    class TrainFileReceiver implements Receiver {

        @Override
        public OutputStream receiveUpload(String fileName, String mimeType) {
            try {
                uploadFile = new File(fileName);
                uploadFile.createNewFile();
                uploadStream = new FileOutputStream(uploadFile);
                return uploadStream;
            } catch (IOException e) {
                LOGGER.warning("Exception during loading the file");
                uploadFileBttn.interruptUpload();
            }
            return null;
        }
    }

    private void uploadSucceeded(SucceededEvent succeededEvent) {
        if (uploadStream == null) {
            try {
                uploadStream.close();
            } catch (IOException e) {
                LOGGER.log(Level.INFO, "Exception during closing file upload stream", e);
            }
        }
        uploadFileName.setValue(uploadFile.getName());
    }

    private void updateProgress(long readBytes, long contentLength) {
        uploadProgress.setVisible(true);
        if (contentLength == -1) {
            uploadProgress.setIndeterminate(true);
        } else {
            uploadProgress.setIndeterminate(false);
            uploadProgress.setValue(((float) readBytes) / ((float) contentLength));
        }
    }

}
