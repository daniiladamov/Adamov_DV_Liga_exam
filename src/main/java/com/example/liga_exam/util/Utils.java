package com.example.liga_exam.util;

import java.time.LocalTime;
import java.time.temporal.ChronoField;

public class Utils {
    public static LocalTime roundTime(LocalTime time){
        long aLong = time.getLong(ChronoField.MINUTE_OF_DAY);
        if (aLong%5!=0)
            aLong=aLong%5>2?((aLong/5) + 1)*5:(aLong/5)*5;
        return LocalTime.of((int) (aLong/60), (int) (aLong%60));
    }
}
