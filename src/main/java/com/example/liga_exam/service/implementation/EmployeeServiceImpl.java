package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.Employee;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.EmployeeRepo;
import com.example.liga_exam.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepo employeeRepo;

    @Value("${exception_message}")
    private String exceptionMessage;

    @Override
    @Transactional
    public void setDiscount(Long id, int min, int max) {
        Employee employee=getEmployee(id);
        employee.setDiscountMax(max);
        employee.setDiscountMin(min);
        employeeRepo.save(employee);
    }

    @Override
    public Employee getEmployee(Long id) {
        return employeeRepo.findById(id).orElseThrow(()->
                new EntityNotFoundException(exceptionMessage+id));
    }
}
