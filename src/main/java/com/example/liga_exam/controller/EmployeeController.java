package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.DiscountDto;
import com.example.liga_exam.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Назначение допустимых пределов предоставляемой работником скидки
     * @param dto модель скидки
     * @param id номер работника
     */
    @PatchMapping("/{id}/set-discount")
    @PreAuthorize("hasRole('ADMIN')")
    public void setDiscount(@Validated @RequestBody DiscountDto dto, @PathVariable Long id){
        employeeService.setDiscount(id, dto.getMin(), dto.getMax());
    }
}
