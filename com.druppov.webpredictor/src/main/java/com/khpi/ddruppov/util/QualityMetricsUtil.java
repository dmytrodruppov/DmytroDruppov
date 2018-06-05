package com.khpi.ddruppov.util;

import java.util.List;

public class QualityMetricsUtil {

    /**
     * Calculate Mean Square Error.
     *
     * @return
     */
    public static double calculateMSE(List<Double> predicted, List<Double> historical) {

        double mse = 0;
        for (int i = 0; i < predicted.size(); i++) {
            if (historical.size() - 1 >= i) {
                mse += Math.pow(predicted.get(i) - historical.get(i), 2);
            }
        }
        mse /= predicted.size();
        return mse;
    }

    public static double calculateAverageRelativeError(List<Double> predicted, List<Double> historical) {
        double mse = 0;
        for (int i = 0; i < predicted.size(); i++) {
            if (historical.size() - 1 >= i) {
                mse += (Math.abs(predicted.get(i) - historical.get(i)));
            }
        }
        mse /= predicted.size();
        return mse;
    }

    public static double calculateAbsoluteRelativeError(List<Double> predicted, List<Double> historical) {
        double mse = 0;
        for (int i = 0; i < predicted.size(); i++) {
            if (historical.size() - 1 <= i) {
                mse += (Math.abs(predicted.get(i) - historical.get(i))) / historical.get(i) * 100;
            }
        }
        mse /= predicted.size();
        return mse;
    }


}
