package com.example.liga_exam.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Getter
@Setter
public class BoxReqDto {

    @NotNull
    private Double ratio;

    @NotNull
    @JsonFormat(pattern = "HH-mm")
    private LocalTime open;

    @NotNull
    @JsonFormat(pattern = "HH-mm")
    private LocalTime close;

}
