package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.EmployeeDto;
import com.example.liga_exam.dto.request.OrderReqDto;
import com.example.liga_exam.dto.response.OrderResDto;
import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.mapper.OrderMapper;
import com.example.liga_exam.service.*;
import com.example.liga_exam.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public Long createOrder(@Validated @RequestBody OrderReqDto orderReqDto, @PathVariable Long id) {
        Set<Operation> operationSet = operationService.getOperations(
                orderReqDto.getServices().stream().map(o -> o.getId()).collect(Collectors.toSet()));
        Order order = orderMapper.toEntity(orderReqDto);
        User user = userService.getUser(id);
        return orderService.createOrder(order, operationSet, user);
    }

    @GetMapping("/{id}/orders")
    public Page<OrderResDto> getOrders(@PathVariable Long id, Pageable pageable) {
        User user = userService.getUser(id);
        Specification<Order> orderSpecification = OrderSpecification.userActiveOrders(user);
        return orderService.getOrders(orderSpecification, pageable).map(o -> orderMapper.toResponse(o));
    }

    @PostMapping("/{id}/make-employee")
    public Long makeEmployee(@Validated @RequestBody EmployeeDto dto, @PathVariable Long id)
            throws InvalidRoleValueException {
        User user = userService.getUser(id);
        Box box = boxService.getBox(dto.getBoxId());
        return employeeService.createEmployee(user, box);
    }
}
