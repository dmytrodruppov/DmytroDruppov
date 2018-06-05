package com.khpi.ddruppov.repository;

import com.khpi.ddruppov.domain.EDataType;
import com.khpi.ddruppov.domain.HistoricalData;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HistoryDataRepository extends MongoRepository<HistoricalData, String> {

    List<HistoricalData> findByType(EDataType type);

}
