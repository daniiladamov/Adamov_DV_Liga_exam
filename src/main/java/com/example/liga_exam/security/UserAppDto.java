package com.example.liga_exam.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAppDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String surname;

    private String username;

    private String password;

    private RoleEnum role;

    private String uuid;
}
