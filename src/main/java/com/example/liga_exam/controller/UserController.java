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
import com.example.liga_exam.util.Utils;
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

    /**
     * Заказы пользователя
     * @param id номер пользователя
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return список заказов пользователя
     */
    @GetMapping("/{id}/orders")
    @PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
    public Page<OrderResDto> getOrders(@PathVariable Long id, Integer pageNumber,
                                       Integer pageSize) {
        User user = userService.getUser(id);
        Pageable pageable= Utils.getPageable(pageNumber, pageSize);
        Specification<Order> orderSpecification = OrderSpecification.userActiveOrders(user);
        return orderService.getOrders(orderSpecification, pageable).map(o -> orderMapper.toResponse(o));
    }

    /**
     * Назначение пользователя работником
     * @param dto модель работника
     * @param id номер пользователя
     * @return id номер вновь созданного работника
     * @throws InvalidRoleValueException если у пользователя любая роль, кроме USER
     */
    @PostMapping("/{id}/make-employee")
    @PreAuthorize("hasRole('ADMIN')")
    public Long makeEmployee(@Validated @RequestBody EmployeeDto dto, @PathVariable Long id)
            throws InvalidRoleValueException {
        User user = userService.getUser(id);
        Box box = boxService.getBox(dto.getBoxId());
        return employeeService.createEmployee(user, box);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public void deleteUser(@PathVariable Long id) {
        User user=userService.getUser(id);
        userService.removeUser(user);
    }
}
