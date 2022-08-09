package com.example.liga_exam.service;

import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public interface OrderService {

    Long createOrder(Order order, Set<Operation> operations, User user) throws AuthenticationException;

    Page<Order> getOrders(OrderSearch orderSearch, Integer pageNumber, Integer pageSize,
                          Box box, User user);

    void cancel(Long id, User user) throws AuthenticationException;

    Order getOrder(Long id);

    BigDecimal getRevenue(LocalDate fromDate, LocalDate toDate);

    BigDecimal doneOrder(Long id, Integer discount, User user)
            throws AuthenticationException;

    Page<Order> getOrders(Specification<Order> specification, Pageable pageable);

    void updateOrder(Long id, Order updatedOrder, Set<Operation> operations, User user)
            throws AuthenticationException;

    void arrived(Long id, User user) throws AuthenticationException;
}
