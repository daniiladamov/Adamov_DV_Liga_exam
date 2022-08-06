package com.example.liga_exam.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class OperationResDto {

    private Long id;

    private String name;

    private BigDecimal cost;
}
