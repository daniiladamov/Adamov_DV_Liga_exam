package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.EmployeeDto;
import com.example.liga_exam.dto.request.OrderReqDto;
import com.example.liga_exam.entity.*;
import com.example.liga_exam.mapper.OrderMapper;
import com.example.liga_exam.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.InvalidRoleValueException;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EmployeeService employeeService;
    private final BoxService boxService;
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final OperationService operationService;


    @PostMapping("/{id}/orders")
    public Long createOrder(@Validated @RequestBody OrderReqDto orderReqDto, @PathVariable Long id){
        Set<Operation> operationSet=operationService.getOperations(
                orderReqDto.getServices().stream().map(o->o.getId()).collect(Collectors.toSet()));
        Order order=orderMapper.toEntity(orderReqDto);
        User user=userService.getUser(id);
        return orderService.createOrder(order, operationSet, user);

    }

    @PostMapping("/{id}/make-employee")
    public Long makeEmployee(@Validated @RequestBody EmployeeDto dto, @PathVariable Long id)
            throws InvalidRoleValueException {
        User user=userService.getUser(id);
        Box box=boxService.getBox(dto.getBoxId());
        return employeeService.createEmployee(user, box);
    }
}
