package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.DiscountDto;
import com.example.liga_exam.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PatchMapping("/{id}/set-discount")
    public void setDiscount(@RequestBody DiscountDto dto, @PathVariable Long id){
        employeeService.setDiscount(id, dto.getMin(), dto.getMax());
    }
}
