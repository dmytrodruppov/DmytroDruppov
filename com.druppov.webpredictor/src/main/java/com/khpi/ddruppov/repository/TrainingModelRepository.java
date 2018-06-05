package com.khpi.ddruppov.repository;

import com.khpi.ddruppov.domain.HistoricalData;
import com.khpi.ddruppov.domain.TrainingModel;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainingModelRepository extends MongoRepository<TrainingModel, String> {

    TrainingModel findByName(String name);

}
