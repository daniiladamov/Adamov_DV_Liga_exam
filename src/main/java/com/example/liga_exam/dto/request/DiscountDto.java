package com.example.liga_exam.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
public class DiscountDto {

    @Min(value = 0)
    @Max(value = 4)
    private Integer min;

    @Min(value = 5)
    @Max(value = 20)
    private Integer max;
}
