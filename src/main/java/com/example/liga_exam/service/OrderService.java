package com.example.liga_exam.service;

import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public interface OrderService {
    Long createOrder(Order order, Set<Operation> operations, User user);
    Page<Order> getOrders(OrderSearch orderSearch, Pageable pageable, BoxService boxService);

    void cancel(Long id);

    Order getOrder(Long id);

    BigDecimal getRevenue(LocalDate fromDate, LocalDate toDate);

    BigDecimal doneOrder(Long id, Integer discount, User user);
}
