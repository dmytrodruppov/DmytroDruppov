package com.khpi.ddruppov.rest.request;

public class PredictForecastRequest {

    private String pathToModel;
    private String pathToDataToPredict;

    public PredictForecastRequest(String pathToModel, String pathToDataToPredict) {
        this.pathToDataToPredict = pathToDataToPredict;
        this.pathToModel = pathToModel;
    }

    public String getPathToModel() {
        return pathToModel;
    }

    public void setPathToModel(String pathToModel) {
        this.pathToModel = pathToModel;
    }

    public String getPathToDataToPredict() {
        return pathToDataToPredict;
    }

    public void setPathToDataToPredict(String pathToDataToPredict) {
        this.pathToDataToPredict = pathToDataToPredict;
    }
}
