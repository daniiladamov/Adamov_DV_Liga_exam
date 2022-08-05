package com.example.liga_exam.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Getter
@Setter
public class BoxReqDto {

    @NotNull
    private double ratio;

    @NotNull
    private LocalTime open;

    @NotNull
    private LocalTime close;
}
