package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.BoxReqDto;
import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.mapper.BoxMapper;
import com.example.liga_exam.service.BoxService;
import com.example.liga_exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/boxes")
@RequiredArgsConstructor

public class BoxController {
    private final BoxService boxService;
    private final BoxMapper boxMapper;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createBox(@Validated @RequestBody BoxReqDto dto) {
        Box box = boxMapper.toEntity(dto);
        return boxService.createBox(box);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateBox(@PathVariable Long id, @Validated @RequestBody BoxReqDto dto){
        Box updateBox=boxMapper.toEntity(dto);
        boxService.updateBox(id, updateBox);
    }
 }
