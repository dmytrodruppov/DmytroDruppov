package com.khpi.ddruppov.helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataConverterHelperApp {


    public static void main(String[] args) throws IOException {
        BigDecimal month = new BigDecimal(18551);
        BigDecimal average = new BigDecimal(525);
        int min = -20;
        int max = 20;
        Random random = new Random();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        //Pattern pattern = Pattern.compile("(\\d{2}\\.)(\\d{2}\\.\\d{4})");

        Map<String, Data> dataMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    return Long.compare(dateFormat.parse(o1).getTime(), dateFormat.parse(o2).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        try (BufferedReader bf = new BufferedReader(new FileReader("C:\\Users\\Dmytro Druppov\\Desktop\\FILE1.txt"))) {
            String line = null;
            int i = 0;
            while ((line = bf.readLine()) != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(line);
                System.out.println("String: " + i++);
                //  Matcher m = pattern.matcher(stringTokenizer.nextToken());
                String date = null;
                //if (m.find()) {
                date = stringTokenizer.nextToken();//m.group(2);
                //}
                String time = stringTokenizer.nextToken();
                String temp = stringTokenizer.nextToken();
                String humidity = stringTokenizer.nextToken();
                String wind = stringTokenizer.nextToken();

                Data data = new Data();
                data.setTemperature(new BigDecimal(temp));
                data.setHumidity(new BigDecimal(humidity));
                data.setWind(new BigDecimal(wind));
                data.setCount(1);

                if (dataMap.get(date) != null) {
                    Data oldData = dataMap.get(date);

                    data.setWind(oldData.getWind().add(data.getWind()));
                    data.setTemperature(oldData.getTemperature().add(data.getTemperature()));
                    data.setHumidity(oldData.getHumidity().add(data.getHumidity()));
                    data.setCount(oldData.getCount() + 1);
                }
                dataMap.put(date, data);

            }
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (Map.Entry<String, Data> dataEntry : dataMap.entrySet()) {
            BigDecimal value;
            int number = random.nextInt(max + 1 - min) + min;
            value = average.add(new BigDecimal(String.valueOf(number)));
            //System.out.println(dataEntry);
            System.out.println(value + "  " + dataEntry.getValue().average());
            sum = sum.add(value);
        }
        System.out.println(sum.toString());

    }
}
