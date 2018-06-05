package com.khpi.ddruppov.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibsvmValidator {

    private static final Pattern PATTERN_MODEL = Pattern.compile("^(\\d+\\.?\\d*)(\\s*\\d+:-?\\d+\\.?\\d*)*$");
    private static final Pattern PATTERN_PREDICTION_DATA = Pattern.compile("(\\d+\\.?\\d*)(\\s*-?\\d+\\.?\\d*)*$");


    public static int validateModel(String pathToFile) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {

            Set<Integer> attributesCount = new HashSet<>();
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                Matcher matcher = PATTERN_MODEL.matcher(line);
                if (matcher.find()) {
                    count = StringUtils.countMatches(line, ":");
                } else {
                    count = 0;
                }
                attributesCount.add(count);

                if (count == 0) {
                    System.out.println("line = [" + line + "]");
                    return 0;
                }

                if (attributesCount.size() != 1) {
                    return -1;
                }
            }
            return count;
        }
    }

    public static boolean validatePredictionData(String pathToFile, int sizeAttribute) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {

            String line;
            int count;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                Matcher matcher = PATTERN_PREDICTION_DATA.matcher(line);
                if (matcher.find()) {
                    StringTokenizer tokenizer = new StringTokenizer(line);

                   count = tokenizer.countTokens();

                } else {
                    count = 0;
                }

                if (sizeAttribute != count) {
                    System.out.println("size = " + sizeAttribute);
                    System.out.println("count = " + count);
                    System.out.println("line = [" + line + "]");
                    return false;
                }
            }
            return true;
        }
    }



//    public static void main(String[] args) throws IOException {
//        Matcher m = PATTERN_MODEL.matcher("530 1:5.8  2:95 3:9");
//        System.out.println(m.matches());
//    }

}
