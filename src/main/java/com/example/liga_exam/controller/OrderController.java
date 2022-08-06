package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.dto.response.BoxResDto;
import com.example.liga_exam.dto.response.OrderResDto;
import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.mapper.OrderMapper;
import com.example.liga_exam.service.BoxService;
import com.example.liga_exam.service.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/orders")
public class OrderController {
    private final OrderService orderService;
    private final BoxService boxService;
    private final OrderMapper orderMapper;

    @PostMapping
    public Page<OrderResDto> getBoxFilter(@RequestBody OrderSearch orderSearch, Pageable pageable){
       return orderService.getOrders(orderSearch, pageable,boxService)
               .map(x->orderMapper.toResponse(x));
    }

    @PatchMapping ("/{id}/canceled-order")
    public void canceledOrder(@PathVariable Long id){
        orderService.cancel(id);
    }

}
