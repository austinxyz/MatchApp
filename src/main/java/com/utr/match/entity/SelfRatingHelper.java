package com.utr.match.entity;

public class SelfRatingHelper {

    private static final String[] LEVELS = {"Below", "Avg", "Good", "Strong", "Top"};
    private static final String[] RATINGS = {"3.0", "3.5", "4.0", "4.5", "5.0"};
    private static final double[][] femaleUTRTable = {
            {2.25d, 2.5d, 2.75d, 2.75d, 2.75d},
            {2.75d, 3.0d, 3.25d, 3.5d, 3.75d},
            {4.0d, 4.25d, 4.5d, 4.75d, 5.0d},
            {5.25d, 5.5d, 5.75d, 6.0d, 6.25d},
            {6.75d, 7.0d, 7.25d, 7.5d, 7.75d}
    };

    private static final double[][] maleUTRTable = {
            {3.25d, 3.5d, 3.75d, 3.75d, 3.75d},
            {4.0d, 4.25d, 4.5d, 4.75d, 5.0d},
            {5.25d, 5.5d, 5.75d, 6.0d, 6.25d},
            {6.75d, 7.0d, 7.25d, 7.5d, 7.75d},
            {8.0d, 8.25d, 8.5d, 8.75d, 9.0d},
    };

    public static double getSelfRatingUTR(String rating, String range, String gender) {
        int rateIndex = getRatingIndex(rating);
        int rangeIndex = getRangeIndex(range);

        if (rateIndex == -1 || rangeIndex == -1) {
            return 0.0d;
        }

        if (gender.equals("F")) {
            return femaleUTRTable[rateIndex][rangeIndex];
        } else {
            return maleUTRTable[rateIndex][rangeIndex];
        }
    }

    private static int getRangeIndex(String range) {
        return findIndexInStringArray(range, LEVELS);
    }

    private static int getRatingIndex(String rating) {
        return findIndexInStringArray(rating, RATINGS);
    }

    private static int findIndexInStringArray(String rating, String[] values) {
        for (int i = 0; i< values.length; i++) {
            if (values[i].equals(rating)) {
                return i;
            }
        }
        return -1;
    }
}
