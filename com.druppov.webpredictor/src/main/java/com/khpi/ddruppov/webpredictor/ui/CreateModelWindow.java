package com.khpi.ddruppov.webpredictor.ui;

import com.khpi.ddruppov.domain.EDataType;
import com.khpi.ddruppov.domain.TrainingModel;
import com.khpi.ddruppov.repository.TrainingModelRepository;
import com.khpi.ddruppov.rest.IPredictorRest;
import com.khpi.ddruppov.rest.request.CreateModelRequest;
import com.khpi.ddruppov.util.LibsvmValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.Upload.SucceededEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateModelWindow extends Window {

    private static final Logger LOGGER = Logger.getLogger(CreateModelWindow.class.toString());
    private IPredictorRest predictorRest;
    private File uploadFile;
    private CreateModelRequest createModelRequest;
    private OutputStream uploadStream;
    private TrainingModelRepository repository;

    /**
     * Form components.
     */
    private FormLayout formLayout = new FormLayout();
    private Button saveModel = new Button("Save");
    private TextField modelName = new TextField("Model Name");
    private Upload uploadFileBttn;
    private ProgressBar uploadProgress = new ProgressBar();
    private Label uploadFileName = new Label("Need to upload training file");
    private ComboBox<EDataType> modelDataType = new ComboBox<>("Data type");

    public CreateModelWindow(IPredictorRest predictorRest, TrainingModelRepository repository, ComboBox<String> view) {
        this.predictorRest = predictorRest;
        this.repository = repository;

        setCaption("Create Training Model");
        setWidth("400px");
        setHeight("350px");
        setModal(true);
        setResizable(false);

        uploadFileBttn = new Upload();
        uploadFileBttn.setCaption("Upload train file");
        uploadFileBttn.setReceiver(this::receiveUpload);
        uploadFileBttn.addSucceededListener(this::uploadSucceeded);
        uploadFileBttn.addProgressListener(this::updateProgress);

        formLayout.setSpacing(true);
        formLayout.setMargin(true);
        formLayout.setHeight("100%");
        formLayout.setWidth("100%");

        uploadProgress.setWidth("100%");
        modelDataType.setItems(EDataType.values());

        modelDataType.setRequiredIndicatorVisible(true);
        modelName.setRequiredIndicatorVisible(true);

        formLayout.addComponent(modelName);
        formLayout.addComponent(modelDataType);
        formLayout.addComponent(uploadFileBttn);
        formLayout.addComponent(uploadProgress);
        formLayout.addComponent(uploadFileName);
        formLayout.addComponent(saveModel);

        saveModel.addClickListener(clickEvent -> {
            if (!validate()) {
                return;
            }
            String name = modelName.getValue();
            String path = uploadFile.getAbsolutePath();
            EDataType dataType = modelDataType.getValue();

            try {
                int count = LibsvmValidator.validateModel(path);

                if (count == 0) {
                    Notification.show("Training file not valid, attributes not found");
                    return;
                }

                if (count == -1) {
                    Notification.show("File not valid. Attributes size is different for rows");
                    return;
                }

                createModelRequest = new CreateModelRequest(name, path);


                predictorRest.train(createModelRequest);

                TrainingModel trainingModel = new TrainingModel();
                trainingModel.setName(name);
                trainingModel.setPathToTrainingFile(path);
                trainingModel.setDataType(dataType);
                trainingModel.setSizeAttributes(count);

                repository.insert(trainingModel);


            } catch (IOException e) {
                e.printStackTrace();
            }
            close();
            view.setItems(predictorRest.getTrainModels());
            view.getDataProvider().refreshAll();
        });

        setContent(formLayout);
    }


    private OutputStream receiveUpload(String fileName, String mimeType) {
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

    private boolean validate() {
        if (modelName.getValue() == null || modelName.getValue().isEmpty()) {
            Notification.show("PPlease fill required field: model name");
            return false;
        }
        if (Arrays.asList(predictorRest.getTrainModels()).contains(modelName.getValue())) {
            Notification.show("Model with this name already exists");
            return false;
        }
        if (modelDataType.getValue() == null) {
            Notification.show("Please fill required field: data type");
        }
        if (uploadFile == null) {
            Notification.show("To create a model you need to upload training file");
            return false;
        }
        return true;
    }


}
