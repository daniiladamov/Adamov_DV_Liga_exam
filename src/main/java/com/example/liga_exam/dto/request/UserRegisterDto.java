package com.example.liga_exam.dto.request;

import com.example.liga_exam.validator.annotation.UniqueUsername;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserRegisterDto {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String surname;

    @Size(min = 4)
    @NotBlank
    @UniqueUsername
    private String username;

    @Size(min = 8)
    @NotBlank
    private String password;
}
