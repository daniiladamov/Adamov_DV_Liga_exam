package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.OrderReqDto;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.mapper.OrderMapper;
import com.example.liga_exam.service.OperationService;
import com.example.liga_exam.service.OrderService;
import com.example.liga_exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
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
}
