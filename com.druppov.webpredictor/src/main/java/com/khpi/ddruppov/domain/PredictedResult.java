package com.khpi.ddruppov.domain;

import java.text.DecimalFormat;
import java.util.Date;

public class PredictedResult {

    private static DecimalFormat df2 = new DecimalFormat(".##");
    private String time;
    private Double prediction;
    private Date date;

    public PredictedResult(String time, Double prediction) {
        this.time = time;
        this.prediction = prediction;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getPrediction() {
        return Double.valueOf(df2.format(prediction));
    }

    public void setPrediction(Double prediction) {
        this.prediction = prediction;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
