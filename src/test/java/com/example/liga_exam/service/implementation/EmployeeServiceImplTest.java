package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.Employee;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.repository.EmployeeRepo;
import com.example.liga_exam.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepo employeeRepo;
    private EmployeeService employeeService;
    private Long id=1L;
    private User user=new User();
    private Employee employee=new Employee();

    public EmployeeServiceImplTest() {
        employeeService=new EmployeeServiceImpl(employeeRepo);
        user.setId(id);
        employee.setUser(user);
        employee.setDiscountMin(4);
        employee.setDiscountMax(7);
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void setDiscount() {

    }

    @Test
    void getEmployee_ExpectedBehavior() {
        Mockito.when(employeeRepo.findById(1L)).thenReturn(Optional.ofNullable(employee));
        Mockito.verify(employeeRepo,Mockito.times(1)).findById(1L);

    }

    @Test
    void createEmployee() {
    }
}