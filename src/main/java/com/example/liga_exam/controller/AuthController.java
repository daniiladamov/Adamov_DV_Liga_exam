package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.AuthDto;
import com.example.liga_exam.dto.request.UserRegisterDto;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.mapper.UserMapper;
import com.example.liga_exam.service.AuthService;
import com.example.liga_exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;
    private final UserService userService;
    private final static String CONFIRM_REGISTRATION="Запись подтверждена, ваш id=";

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public String registration(@Validated @RequestBody UserRegisterDto dto){
        User user=userMapper.toEntity(dto);
        return userService.registerUser(user);
    }
    @PostMapping("/login")
    public void login(@Validated @RequestBody AuthDto dto){
        authService.auth(dto.getUsername(), dto.getPassword());
    }

    @GetMapping("/confirm/{id}")
    public String confirmRegistration(@PathVariable Long id) {
        userService.confirmUser(id);
        return CONFIRM_REGISTRATION+id;
    }
}
