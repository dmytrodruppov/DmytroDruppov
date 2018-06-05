package com.khpi.ddruppov.rest.request;

import com.khpi.ddruppov.domain.EDataType;

public class CreateModelRequest {

    private String pathToTrainingFile;
    private EDataType dataType;
    private String modelName;
    private int countAttributes;

    public CreateModelRequest(String modelName, String pathToTrainingFile) {
        this.modelName = modelName;
        this.pathToTrainingFile = pathToTrainingFile;
    }

    public String getPathToTrainingFile() {
        return pathToTrainingFile;
    }

    public void setPathToTrainingFile(String pathToTrainingFile) {
        this.pathToTrainingFile = pathToTrainingFile;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public EDataType getDataType() {
        return dataType;
    }

    public void setDataType(EDataType dataType) {
        this.dataType = dataType;
    }

    public int getCountAttributes() {
        return countAttributes;
    }

    public void setCountAttributes(int countAttributes) {
        this.countAttributes = countAttributes;
    }
}
