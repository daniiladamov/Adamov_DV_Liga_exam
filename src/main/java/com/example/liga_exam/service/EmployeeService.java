package com.example.liga_exam.service;

import com.example.liga_exam.entity.Employee;

public interface EmployeeService {

    void setDiscount(Long id, int min, int max);

    Employee getEmployee(Long id);
}
