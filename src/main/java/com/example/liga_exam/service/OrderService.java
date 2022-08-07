package com.example.liga_exam.service;

import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface OrderService {
    String REMARK_FOR_BOX = "В Box#%d услуги выполняются с отклонением от графика";
    String EXCEPTION_TIME = "Нельзя завершать услугу до времени ее начала - %s";
    String CANCELED_ORDER = "Заказ был отменен ранее, операция недоступна";
    String DONE_ORDER = "Заказ был выполнен ранее, операция недоступна";
    String INVALID_INTERVAL = "Временной интервал задан неверно";
    String INVALID_DISCOUNT="Можно назначать скидку в пределах %d% - %d%";
    String DISCOUNT_NOT_AVAILABLE="Работнику запрещено назначать скидку";
    String INVALID_ORDER_DATE="Запись на прошедшие даты не доступна";
    String INVALID_ORDER_TIME="Запись допступна минимум на %d минут более текущего времени";
    Long createOrder(Order order, Set<Operation> operations, User user);
    Page<Order> getOrders(OrderSearch orderSearch, Pageable pageable, BoxService boxService);

    void cancel(Long id);

    Order getOrder(Long id);

    BigDecimal getRevenue(LocalDate fromDate, LocalDate toDate);

    BigDecimal doneOrder(Long id, Integer discount, User user);

    Page<Order> getOrders(Specification<Order> specification, Pageable pageable);
}
