package com.khpi.ddruppov.helper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Data {

    private BigDecimal temperature = BigDecimal.ZERO;
    private BigDecimal humidity = BigDecimal.ZERO;
    private BigDecimal wind = BigDecimal.ZERO;
    private long count;

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public BigDecimal getHumidity() {
        return humidity;
    }

    public void setHumidity(BigDecimal humidity) {
        this.humidity = humidity;
    }

    public BigDecimal getWind() {
        return wind;
    }

    public void setWind(BigDecimal wind) {
        this.wind = wind;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String average() {
        return "1:" +temperature.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP) + "  2:" +
                humidity.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP) + "  3:" +
                wind.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "Data{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", wind=" + wind +
                ", count=" + count +
                '}';
    }
}
