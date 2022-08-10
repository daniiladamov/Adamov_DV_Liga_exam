package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.Employee;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.EmployeeRepo;
import com.example.liga_exam.security.RoleEnum;
import com.example.liga_exam.service.EmployeeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Any;

import javax.management.relation.InvalidRoleValueException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepo employeeRepo;
    private EmployeeService employeeService;
    private Long id=1L;
    private User user=new User();
    private Employee employee=new Employee();
    private Box box=new Box();

    public EmployeeServiceImplTest() {
        MockitoAnnotations.openMocks(this);
        employeeService=new EmployeeServiceImpl(employeeRepo);
        user.setId(id);
        user.setRole(RoleEnum.ROLE_USER);
        employee.setId(id);
        employee.setUser(user);
        employee.setDiscountMin(4);
        employee.setDiscountMax(7);
        box.setId(id);
    }

    @Test
    void setDiscount_ExpectedBehavior() {
        Mockito.when(employeeRepo.save(employee)).thenReturn(employee);
        Mockito.when(employeeRepo.findById(id)).thenReturn(Optional.ofNullable(employee));
        employeeService.setDiscount(id,4,10);
        Mockito.verify(employeeRepo,Mockito.times(1)).findById(id);
        Mockito.verify(employeeRepo,Mockito.times(1)).save(employee);
    }

    @Test
    void setDiscount_EmployeeNotFound_ExpectedBehavior() {
        Mockito.when(employeeRepo.findById(id)).thenReturn(Optional.ofNullable(null));
        Throwable throwable=Assertions.assertThrows(EntityNotFoundException.class, ()->
                employeeService.setDiscount(id,4,10));
        Assertions.assertNotNull(throwable);
        Mockito.verify(employeeRepo,Mockito.times(1)).findById(id);

    }

    @Test
    void getEmployee_ExpectedBehavior() {
        Mockito.when(employeeRepo.findById(id)).thenReturn(Optional.ofNullable(employee));
        Employee empl = employeeService.getEmployee(id);
        Mockito.verify(employeeRepo,Mockito.times(1)).findById(id);
        Assertions.assertEquals(empl.getId(),id);
    }
    @Test
    void getEmployee_EmployeeNotFound_ExpectedBehavior() {
        Mockito.when(employeeRepo.findById(id)).thenReturn(Optional.ofNullable(null));
        Throwable throwable=Assertions.assertThrows(EntityNotFoundException.class,()->
                employeeService.getEmployee(id));
        Assertions.assertNotNull(throwable);
        Mockito.verify(employeeRepo,Mockito.times(1)).findById(id);
    }
    @Test
    void createEmployee_ExpectedBehavior() throws InvalidRoleValueException {
        Mockito.when(employeeRepo.save(Mockito.any(Employee.class))).thenReturn(employee);
        Long emplId = employeeService.createEmployee(user, box);
        Mockito.verify(employeeRepo, Mockito.times(1)).save(Mockito.any(Employee.class));
        Assertions.assertEquals(emplId,employee.getId());
    }
    @Test
    void createEmployee_InvalidUserRole_ExpectedBehavior(){
        user.setRole(RoleEnum.ROLE_ADMIN);
        Throwable throwable=Assertions.assertThrows(InvalidRoleValueException.class, ()->
                employeeService.createEmployee(user, box));
        Assertions.assertNotNull(throwable);
        user.setRole(RoleEnum.ROLE_EMPLOYEE);
        throwable=Assertions.assertThrows(InvalidRoleValueException.class, ()->
                employeeService.createEmployee(user, box));
        Assertions.assertNotNull(throwable);
        user.setRole(RoleEnum.REMOVED);
        throwable=Assertions.assertThrows(InvalidRoleValueException.class, ()->
                employeeService.createEmployee(user, box));
        Assertions.assertNotNull(throwable);
    }
}