package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.OrderReqDto;
import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.dto.request.PeriodDto;
import com.example.liga_exam.dto.response.OrderResDto;
import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.mapper.OrderMapper;
import com.example.liga_exam.service.*;
import com.example.liga_exam.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.util.Objects;
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
    private final static String CONFIRM_REGISTRATION = "Запись подтверждена, номер заказа id=";

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public String createOrder(@Validated @RequestBody OrderReqDto orderReqDto)
            throws AuthenticationException {
        Set<Operation> operationSet = operationService.getOperations(
                orderReqDto.getServices().stream().map(o -> o.getId()).collect(Collectors.toSet()));
        Order order = orderMapper.toEntity(orderReqDto);
        User user = userService.getUserByUsername(authService.getUsername());
        return orderService.createOrder(order, operationSet, user);
    }
    @GetMapping("/{id}/confirm")
    public String confirmRegistration(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return CONFIRM_REGISTRATION + id;
    }

    @PostMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public Page<OrderResDto> getOrders(Integer pageSize,
                                       Integer pageNumber,
                                       @Validated @RequestBody OrderSearch orderSearch) {
        Box box = null;
        if (Objects.nonNull(orderSearch.getBoxId()))
            box = boxService.getBox(orderSearch.getBoxId());
        User user = userService.getUserByUsername(authService.getUsername());
        Pageable pageable= Utils.getPageable(pageNumber, pageSize);
        return orderService.getOrders(orderSearch, pageable, box, user)
                .map(x -> orderMapper.toResponse(x));
    }

    @PostMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public BigDecimal getRevenue(@RequestBody PeriodDto dto) {
        return orderService.getRevenue(dto.getFromDate(), dto.getToDate());
    }

    @PatchMapping("/{id}/customer-arrived")
    public void customerArrivedInTime(@PathVariable Long id)
            throws AuthenticationException {
        User user = userService.getUserByUsername(authService.getUsername());
        orderService.arrived(id, user);
    }

    @PatchMapping("/{id}/cancel-order")
    public void canceledOrder(@PathVariable Long id) throws AuthenticationException {
        User user = userService.getUserByUsername(authService.getUsername());
        orderService.cancel(id, user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','USER')")
    public String changeOrder(@PathVariable Long id, @Validated @RequestBody OrderReqDto dto)
            throws AuthenticationException {
        User user = userService.getUserByUsername(authService.getUsername());
        Set<Operation> operationSet = operationService.getOperations(
                dto.getServices().stream().map(o -> o.getId()).collect(Collectors.toSet()));
        return orderService.updateOrder(id, orderMapper.toEntity(dto), operationSet, user);
    }

    @PatchMapping("/{id}/done-order")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public BigDecimal doneOrder(@PathVariable Long id, Integer discount)
            throws AuthenticationException {
        User user = userService.getUserByUsername(authService.getUsername());
        return orderService.doneOrder(id, discount, user);
    }
}
