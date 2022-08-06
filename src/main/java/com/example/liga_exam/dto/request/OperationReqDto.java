package com.example.liga_exam.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class OperationReqDto {
    @NotBlank
    private Long id;
}
