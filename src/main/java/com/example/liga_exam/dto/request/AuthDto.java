package com.example.liga_exam.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class AuthDto {

    @Size(min = 4, max = 64)
    @NotBlank
    private String username;

    @Size(min = 8, max = 64)
    @NotBlank
    private String password;
}
