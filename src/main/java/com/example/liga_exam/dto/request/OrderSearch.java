package com.example.liga_exam.dto.request;

import com.example.liga_exam.validator.annotation.ExistingBox;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.utility.nullability.MaybeNull;

import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class OrderSearch {

    @ExistingBox
    private Long boxId;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    @JsonFormat(pattern = "HH-mm")
    private LocalTime start;
}
