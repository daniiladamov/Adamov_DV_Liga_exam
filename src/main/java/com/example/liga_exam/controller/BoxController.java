package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.BoxReqDto;
import com.example.liga_exam.entity.Box;
import com.example.liga_exam.mapper.BoxMapper;
import com.example.liga_exam.service.BoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/boxes")
@RequiredArgsConstructor

public class BoxController {
    private final BoxService boxService;
    private final BoxMapper boxMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createBox(@Validated @RequestBody BoxReqDto dto) {
        Box box = boxMapper.toEntity(dto);
        return boxService.createBox(box);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateBox(@PathVariable Long id, @Validated @RequestBody BoxReqDto dto) {
        Box updateBox = boxMapper.toEntity(dto);
        boxService.updateBox(id, updateBox);
    }
}
