package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.EmployeeDto;
import com.example.liga_exam.dto.response.OrderResDto;
import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.mapper.OrderMapper;
import com.example.liga_exam.service.BoxService;
import com.example.liga_exam.service.EmployeeService;
import com.example.liga_exam.service.OrderService;
import com.example.liga_exam.service.UserService;
import com.example.liga_exam.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.InvalidRoleValueException;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EmployeeService employeeService;
    private final BoxService boxService;
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping("/{id}/orders")
    @PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
    public Page<OrderResDto> getOrders(@PathVariable Long id, Pageable pageable) {
        User user = userService.getUser(id);
        Specification<Order> orderSpecification = OrderSpecification.userActiveOrders(user);
        return orderService.getOrders(orderSpecification, pageable).map(o -> orderMapper.toResponse(o));
    }

    @PostMapping("/{id}/make-employee")
    @PreAuthorize("hasRole('ADMIN')")
    public Long makeEmployee(@Validated @RequestBody EmployeeDto dto, @PathVariable Long id)
            throws InvalidRoleValueException {
        User user = userService.getUser(id);
        Box box = boxService.getBox(dto.getBoxId());
        return employeeService.createEmployee(user, box);
    }
}
