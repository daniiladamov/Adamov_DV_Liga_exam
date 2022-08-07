package com.example.liga_exam.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OperationReqDto {
    @NotNull
    private Long id;
}
