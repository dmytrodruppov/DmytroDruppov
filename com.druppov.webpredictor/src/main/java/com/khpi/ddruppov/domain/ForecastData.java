package com.khpi.ddruppov.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
public class ForecastData {

    @Id
    private String id;
    private Date date;
    private EDataType type;
    private List<PredictedResult> result;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<PredictedResult> getResult() {
        return result;
    }

    public void setResult(List<PredictedResult> result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EDataType getType() {
        return type;
    }

    public void setType(EDataType dataType) {
        this.type = dataType;
    }
}
