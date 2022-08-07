package com.example.liga_exam.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class EmployeeDto {

    @NotNull
    private Long boxId;

}
