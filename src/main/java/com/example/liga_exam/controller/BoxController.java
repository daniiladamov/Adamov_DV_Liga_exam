package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.BoxReqDto;
import com.example.liga_exam.entity.Box;
import com.example.liga_exam.mapper.BoxMapper;
import com.example.liga_exam.service.BoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/boxes")
@RequiredArgsConstructor
public class BoxController {
    private final BoxService boxService;
    private final BoxMapper boxMapper;

    @PostMapping
    public Long createBox(@Validated @RequestBody BoxReqDto boxReqDto){
        Box box=boxMapper.toEntity(boxReqDto);
        return boxService.createBox(box);
    }
}
