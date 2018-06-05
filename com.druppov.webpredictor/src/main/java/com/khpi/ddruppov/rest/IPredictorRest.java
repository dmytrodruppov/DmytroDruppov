package com.khpi.ddruppov.rest;

import com.khpi.ddruppov.rest.request.CreateModelRequest;
import com.khpi.ddruppov.rest.request.PredictForecastRequest;

import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface IPredictorRest {

    HttpStatus train(CreateModelRequest request) throws IOException;

    List<Double> predict(PredictForecastRequest request) throws IOException;

    String[] getTrainModels();
}
