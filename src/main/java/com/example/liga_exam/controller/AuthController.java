package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.AuthDto;
import com.example.liga_exam.dto.request.JwtRefreshDto;
import com.example.liga_exam.dto.request.UserRegisterDto;
import com.example.liga_exam.dto.response.JwtResDto;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.mapper.UserMapper;
import com.example.liga_exam.service.AuthService;
import com.example.liga_exam.service.JwtService;
import com.example.liga_exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;
    private final UserMapper userMapper;
    private final UserService userService;
    private final static String CONFIRM_REGISTRATION = "Запись подтверждена, ваш id=";

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public String registration(@Validated @RequestBody UserRegisterDto dto) {
        User user = userMapper.toEntity(dto);
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public JwtResDto login(@Validated @RequestBody AuthDto dto) {
        authService.auth(dto.getUsername(), dto.getPassword());
        return jwtService.generateTokens(dto.getUsername());
    }

    @GetMapping("/confirm/{id}")
    public String confirmRegistration(@PathVariable Long id) {

        userService.confirmUser(id);
        return CONFIRM_REGISTRATION + id;
    }
    @PostMapping("jwt-refresh")
    public JwtResDto refreshAllTokens(@RequestBody JwtRefreshDto dto){
        String username= jwtService.validateJwtRefreshToken(dto.getRefreshToken());
        return jwtService.generateTokens(username);
    }

}
