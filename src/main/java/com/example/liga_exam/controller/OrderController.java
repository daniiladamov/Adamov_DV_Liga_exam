package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.OrderReqDto;
import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.dto.request.PeriodDto;
import com.example.liga_exam.dto.response.OrderResDto;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.mapper.OrderMapper;
import com.example.liga_exam.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/orders")
public class OrderController {
    private final OperationService operationService;
    private final OrderService orderService;
    private final UserService userService;
    private final BoxService boxService;
    private final OrderMapper orderMapper;
    private final AuthService authService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN') || hasRole('USER')")
    public Long createOrder(@Validated @RequestBody OrderReqDto orderReqDto) {
        Set<Operation> operationSet = operationService.getOperations(
                orderReqDto.getServices().stream().map(o -> o.getId()).collect(Collectors.toSet()));
        Order order = orderMapper.toEntity(orderReqDto);
        String username=SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        return orderService.createOrder(order, operationSet, user);
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderResDto> getBoxFilter(OrderSearch orderSearch, Pageable pageable) {
        return orderService.getOrders(orderSearch, pageable, boxService)
                .map(x -> orderMapper.toResponse(x));
    }

    @PostMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public BigDecimal getRevenue(@RequestBody PeriodDto dto) {
        return orderService.getRevenue(dto.getFromDate(), dto.getToDate());
    }

    @PatchMapping("/{id}/customer-arrived")
    public void customerArrivedInTime(@PathVariable Long id) throws AuthenticationException {
        User user=userService.getUserByUsername(authService.getUsername());
        orderService.arrived(id, user);
    }

    @PatchMapping("/{id}/canceled-order")
    public void canceledOrder(@PathVariable Long id) throws AuthenticationException {
        User user=userService.getUserByUsername(authService.getUsername());
        orderService.cancel(id, user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN') || hasRole('USER')")
    public void changeOrder(@PathVariable Long id, @Validated @RequestBody OrderReqDto dto)
            throws AuthenticationException {
        User user=userService.getUserByUsername(authService.getUsername());
        Set<Operation> operationSet = operationService.getOperations(
                dto.getServices().stream().map(o -> o.getId()).collect(Collectors.toSet()));
        orderService.updateOrder(id,orderMapper.toEntity(dto), operationSet, user);
    }

    @PatchMapping("/{id}/done-order")
    @PreAuthorize("hasAnyRole('ADMIN') || hasRole('EMPLOYEE')")
    public BigDecimal doneOrder(@PathVariable Long id, Integer discount) throws AuthenticationException {
        User user=userService.getUserByUsername(authService.getUsername());
        return orderService.doneOrder(id,discount, user);
    }
}
