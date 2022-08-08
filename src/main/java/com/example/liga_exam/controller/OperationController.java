package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.OperationRegisterDto;
import com.example.liga_exam.dto.response.OperationResDto;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.mapper.OperationMapper;
import com.example.liga_exam.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/services")
@RequiredArgsConstructor
public class OperationController {
    private final OperationService operationService;
    private final OperationMapper operationMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Long createOperation(@Validated @RequestBody OperationRegisterDto dto) {
        Operation operation = operationMapper.toEntity(dto);
        return operationService.createService(operation);
    }
    @GetMapping
    public Page<OperationResDto> getOperations(Pageable pageable){
        return operationService.getOperations(pageable).map(op ->
                operationMapper.toResponse(op));
    }
}
