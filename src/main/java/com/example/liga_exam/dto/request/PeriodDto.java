package com.example.liga_exam.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PeriodDto {

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fromDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate toDate;
}
