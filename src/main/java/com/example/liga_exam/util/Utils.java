package com.example.liga_exam.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Objects;

public class Utils {
    public static LocalTime roundTime(LocalTime time){
        long aLong = time.getLong(ChronoField.MINUTE_OF_DAY);
        if (aLong%5!=0)
            aLong=aLong%5>2?((aLong/5) + 1)*5:(aLong/5)*5;
        return LocalTime.of((int) (aLong/60), (int) (aLong%60));
    }
    public static Pageable getPageable(Integer pageNumber, Integer pageSize){
        if (Objects.isNull(pageNumber))
            pageNumber = 0;
        if (Objects.isNull(pageSize))
            pageSize = 5;
        return PageRequest.of(pageNumber, pageSize);
    }
}
