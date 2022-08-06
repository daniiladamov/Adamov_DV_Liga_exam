package com.example.liga_exam.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
public class OrderReqDto {
    
    @NotNull
    @JsonFormat(pattern = "HH-mm")
    private LocalTime startTime;

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    @NotBlank
    private Set<OperationReqDto> services;
}
