package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.OperationRegisterDto;
import com.example.liga_exam.dto.response.OperationResDto;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.mapper.OperationMapper;
import com.example.liga_exam.service.OperationService;
import com.example.liga_exam.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/operations")
@RequiredArgsConstructor
public class OperationController {
    private final OperationService operationService;
    private final OperationMapper operationMapper;

    /**
     * Создание предоставляемой услуги
     * @param dto модель услуги
     * @return номер id вновь созданной услуги
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Long createOperation(@Validated @RequestBody OperationRegisterDto dto) {
        Operation operation = operationMapper.toEntity(dto);
        return operationService.createService(operation);
    }

    /**
     * Список предоставляемых услуг
     * @param pageNumber номер страницы
     * @param pageSize размер старницы
     * @return страницу предоставляемых услуг
     */
    @GetMapping
    public Page<OperationResDto> getOperations(Integer pageNumber, Integer pageSize){
        Pageable pageable= Utils.getPageable(pageNumber, pageSize);
        return operationService.getOperations(pageable).map(op -> operationMapper.toResponse(op));
    }
}
