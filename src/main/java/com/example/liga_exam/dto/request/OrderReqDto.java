package com.example.liga_exam.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @Size(min = 1)
    private Set<OperationReqDto> services;
}
