package com.khpi.ddruppov.domain;

import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

public class HistoricalData {

    @Id
    private String id;
    private Date date;
    private EDataType type;
    private List<PredictedResult> values;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EDataType getType() {
        return type;
    }

    public void setType(EDataType type) {
        this.type = type;
    }

    public List<PredictedResult> getValues() {
        return values;
    }

    public void setValues(List<PredictedResult> values) {
        this.values = values;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
