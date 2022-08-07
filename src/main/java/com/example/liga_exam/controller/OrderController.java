package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.OrderReqDto;
import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.dto.request.PeriodDto;
import com.example.liga_exam.dto.response.OrderResDto;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.mapper.OrderMapper;
import com.example.liga_exam.service.BoxService;
import com.example.liga_exam.service.OperationService;
import com.example.liga_exam.service.OrderService;
import com.example.liga_exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public Page<OrderResDto> getBoxFilter(@RequestBody OrderSearch orderSearch, Pageable pageable) {
        return orderService.getOrders(orderSearch, pageable, boxService)
                .map(x -> orderMapper.toResponse(x));
    }

    @PostMapping("/revenue")
    public BigDecimal getRevenue(@RequestBody PeriodDto dto) {
        return orderService.getRevenue(dto.getFromDate(), dto.getToDate());
    }

    @PatchMapping("/{id}/canceled-order")
    public void canceledOrder(@PathVariable Long id) {
        orderService.cancel(id);
    }

    @PutMapping("/{id}")
    public void changeOrder(@PathVariable Long id, @Validated @RequestBody OrderReqDto dto){
        String username=SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userService.getUserByUsername(username);
        Set<Operation> operationSet = operationService.getOperations(
                dto.getServices().stream().map(o -> o.getId()).collect(Collectors.toSet()));
        orderService.updateOrder(id,orderMapper.toEntity(dto), operationSet, user);
    }

    @PatchMapping("/{id}/done-order")
    public BigDecimal doneOrder(@PathVariable Long id, Integer discount){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userService.getUserByUsername(username);
        return orderService.doneOrder(id,discount, user);
    }

}
