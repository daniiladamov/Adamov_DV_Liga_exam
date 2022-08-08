package com.example.liga_exam.dto.response;

import com.example.liga_exam.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
public class OrderResDto {

    private Long id;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalDate date;

    private OrderStatus orderStatus;

    private Set<OperationResDto> operations;

    private UserResDto user;

    private BoxResDto box;
}

