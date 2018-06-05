package com.khpi.ddruppov.webpredictor;

import com.khpi.ddruppov.domain.EDataType;
import com.khpi.ddruppov.domain.HistoricalData;
import com.khpi.ddruppov.domain.PredictedResult;
import com.khpi.ddruppov.repository.ForecastDataRepository;
import com.khpi.ddruppov.repository.HistoryDataRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = ForecastDataRepository.class)
@ComponentScan("com.khpi.ddruppov")
public class Application implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger(Application.class.toString());
    private static final DateFormat dateFormat = new SimpleDateFormat("MMM-yyyy");
    private static final Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})");

    @Autowired
    HistoryDataRepository repository;

    public static void main(String[] args) {
       SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {


        int year = 0;
        int month = 0;

        try (BufferedReader bf = new BufferedReader(new FileReader("trainSetNew"))) {
            String line = null;
            while ((line = bf.readLine()) != null) {

                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    year = Integer.valueOf(matcher.group(1));
                    month = Integer.valueOf(matcher.group(2));
                } else {
                    return;
                }
                List<PredictedResult> predictedResults = new ArrayList<>();
                int i = 1;
                while (i < 25 && (line = bf.readLine()) != null) {

                    StringTokenizer tokenizer = new StringTokenizer(line);
                    //LOGGER.info("value is: " + tokenizer.nextToken());
                    Double value = Double.valueOf(tokenizer.nextToken());
                    //LOGGER.info("value is: " + value);
                    predictedResults.add(new PredictedResult(String.valueOf(i++), value));
                }

                Date date = new Date(year - 1900, month, 0);

                HistoricalData historicalData = new HistoricalData();
                historicalData.setValues(predictedResults);
                historicalData.setType(EDataType.PRICE_DAILY);
                historicalData.setId(dateFormat.format(date) + " - Price");
                historicalData.setDate(date);
                repository.save(historicalData);
            }
        }

        try (BufferedReader bf = new BufferedReader(new FileReader("tempTrainSet.txt"))) {
            String line = null;
            while ((line = bf.readLine()) != null) {

                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    year = Integer.valueOf(matcher.group(1));
                    month = Integer.valueOf(matcher.group(2));
                } else {
                    return;
                }
                List<PredictedResult> predictedResults = new ArrayList<>();
                int i = 1;

                YearMonth yearMonthObject = YearMonth.of(year, month);
                int daysInMonth = yearMonthObject.lengthOfMonth();

                while (i <= daysInMonth && (line = bf.readLine()) != null) {

                    StringTokenizer tokenizer = new StringTokenizer(line);
                    //LOGGER.info("value is: " + tokenizer.nextToken());
                    Double value = Double.valueOf(tokenizer.nextToken());
                    //LOGGER.info("value is: " + value);
                    predictedResults.add(new PredictedResult(String.valueOf(i++), value));
                }

                Date date = new Date(year - 1900, month, 0);

                HistoricalData historicalData = new HistoricalData();
                historicalData.setValues(predictedResults);
                historicalData.setType(EDataType.WEATHER_MONTHLY);
                historicalData.setId(dateFormat.format(date) + " - Weather");
                historicalData.setDate(date);
                repository.save(historicalData);
            }
        }

    }

}
