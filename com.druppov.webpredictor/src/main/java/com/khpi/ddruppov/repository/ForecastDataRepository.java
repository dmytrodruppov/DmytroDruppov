package com.khpi.ddruppov.repository;

import com.khpi.ddruppov.domain.EDataType;
import com.khpi.ddruppov.domain.ForecastData;
import com.khpi.ddruppov.domain.HistoricalData;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ForecastDataRepository extends MongoRepository<ForecastData, String> {
    List<ForecastData> findByType(EDataType type);
}
