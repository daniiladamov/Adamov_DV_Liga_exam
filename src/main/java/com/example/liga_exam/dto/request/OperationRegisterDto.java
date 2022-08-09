package com.example.liga_exam.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class OperationRegisterDto {

    @NotBlank
    private String name;

    @NotNull
    @Min(value = 10)
    private Integer duration;

    @NotNull
    @Min(value = 100)
    private BigDecimal cost;
}
