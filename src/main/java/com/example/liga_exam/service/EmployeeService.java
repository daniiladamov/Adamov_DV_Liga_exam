package com.example.liga_exam.service;

import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.Employee;
import com.example.liga_exam.entity.User;

import javax.management.relation.InvalidRoleValueException;

public interface EmployeeService {

    void setDiscount(Long id, int min, int max);

    Employee getEmployee(Long id);

    Long createEmployee(User user, Box box) throws InvalidRoleValueException;
}
