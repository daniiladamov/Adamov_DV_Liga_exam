package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.Employee;
import com.example.liga_exam.entity.RoleEnum;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.EmployeeRepo;
import com.example.liga_exam.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.InvalidRoleValueException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepo employeeRepo;

    @Value("${exception_message}")
    private String exceptionMessage;

    @Value("${invalid_role}")
    private String invalidMessage;

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

    @Override
    @Transactional
    public Long createEmployee(User user, Box box) throws InvalidRoleValueException {
        if (!user.getRole().equals(RoleEnum.USER))
            throw new InvalidRoleValueException(String.format(invalidMessage,user.getId(),
                    user.getRole().toString()));
        user.setRole(RoleEnum.EMPLOYEE);
        Employee employee=new Employee();
        employee.setUser(user);
        employee.setBox(box);
        return employeeRepo.save(employee).getId();
    }
}
