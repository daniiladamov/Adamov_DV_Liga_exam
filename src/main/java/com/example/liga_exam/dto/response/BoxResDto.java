package com.example.liga_exam.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class BoxResDto {

    private Long id;

    private LocalTime open;

    private LocalTime close;
}
