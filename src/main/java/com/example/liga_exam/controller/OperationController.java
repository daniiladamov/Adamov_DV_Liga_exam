package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.OperationRegisterDto;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.mapper.OperationMapper;
import com.example.liga_exam.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/services")
@RequiredArgsConstructor
public class OperationController {
    private final OperationService operationService;
    private final OperationMapper operationMapper;

    @PostMapping
    public Long createOperation(@Validated @RequestBody OperationRegisterDto dto) {
        Operation operation = operationMapper.toEntity(dto);
        return operationService.createService(operation);
    }
}
