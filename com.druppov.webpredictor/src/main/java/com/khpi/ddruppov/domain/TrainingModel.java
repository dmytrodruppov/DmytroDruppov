package com.khpi.ddruppov.domain;

import org.springframework.data.annotation.Id;

public class TrainingModel {

    @Id
    private String name;
    private String pathToModel;
    private String pathToTrainingFile;
    private EDataType dataType;
    private int sizeAttributes;

    public String getName() {
        return name;
    }

    public void setName(String nameModel) {
        this.name = nameModel;
    }

    public String getPathToModel() {
        return pathToModel;
    }

    public void setPathToModel(String pathToModel) {
        this.pathToModel = pathToModel;
    }

    public String getPathToTrainingFile() {
        return pathToTrainingFile;
    }

    public void setPathToTrainingFile(String pathToTrainingFile) {
        this.pathToTrainingFile = pathToTrainingFile;
    }

    public EDataType getDataType() {
        return dataType;
    }

    public void setDataType(EDataType dataType) {
        this.dataType = dataType;
    }

    public int getSizeAttributes() {
        return sizeAttributes;
    }

    public void setSizeAttributes(int sizeAttributes) {
        this.sizeAttributes = sizeAttributes;
    }
}
