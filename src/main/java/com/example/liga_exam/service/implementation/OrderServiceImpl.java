package com.example.liga_exam.service.implementation;

import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.entity.*;
import com.example.liga_exam.exception.DiscountException;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.exception.OrderWasCanceledException;
import com.example.liga_exam.exception.OrderWasDoneException;
import com.example.liga_exam.repository.BoxRepo;
import com.example.liga_exam.repository.OrderRepo;
import com.example.liga_exam.service.BoxService;
import com.example.liga_exam.service.OrderService;
import com.example.liga_exam.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final BoxRepo boxRepo;
    @Value("${exception_message}")
    private String exceptionMessage;

    private static final String REMARK_FOR_BOX = "В Box#%d услуги выполняются с отклонением от графика";
    private static final String EXCEPTION_TIME = "Нельзя завершать услугу до времени ее начала - %s";
    private static final String CANCELED_ORDER = "Заказ был отменен ранее, операция недоступна";
    private static final String DONE_ORDER = "Заказ был выполнен ранее, операция недоступна";
    private static final String INVALID_INTERVAL = "Временной интервал задан неверно";
    private static final String INVALID_DISCOUNT="Можно назначать скидку в пределах %d% - %d%";
    private static final String DISCOUNT_NOT_AVAILABLE="Работнику запрещено назначать скидку";

    @Transactional
    @Override
    public Long createOrder(Order order, Set<Operation> operations, User user) {
        int duration = operations.stream().mapToInt(o -> o.getDuration()).sum();
        LocalTime startTime = order.getStartTime();
        List<Box> freeBoxes = boxRepo.getFreeBoxes(order.getDate(), startTime.getHour(), startTime.getMinute(), duration);
        Collections.shuffle(freeBoxes);
        order.setBox(freeBoxes.get(0));
        order.setUser(user);
        BigDecimal cost = operations.stream().map(op -> op.getCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
        cost.setScale(2, RoundingMode.CEILING);
        order.setCost(cost);
        double calculate = duration * freeBoxes.get(0).getRatio();
        LocalTime endTime = order.getStartTime().plusMinutes((long) Math.ceil(calculate));
        order.setEndTime(endTime);
        return orderRepo.save(order).getId();
    }

    @Override
    public Page<Order> getOrders(OrderSearch orderSearch, Pageable pageable, BoxService boxService) {
        OrderSpecification orderSpecification = new OrderSpecification(orderSearch, boxService);
        return orderRepo.findAll(Specification.where(orderSpecification), pageable);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Order order = getOrder(id);
        checkOrderStatus(order);
        order.setActive(false);
        orderRepo.save(order);
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException(exceptionMessage + id));
    }

    @Override
    public BigDecimal getRevenue(LocalDate fromDate, LocalDate toDate) {
        if (Objects.nonNull(fromDate) && Objects.nonNull(toDate) && fromDate.compareTo(toDate) > 0) {
            throw new DateTimeException(INVALID_INTERVAL);
        }
        List<Order> revenue = orderRepo.findAll(Specification.where(
                OrderSpecification.revenuePredicate(fromDate, toDate)));
        return revenue.stream().map(rev -> rev.getCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public BigDecimal doneOrder(Long orderId, Integer discount, User user) {
        Order order = getOrder(orderId);
        checkOrderStatus(order)
                .checkDateOrder(order)
                .checkDiscountOrder(order, discount, user);
        order.setDone(true);
        order.setActive(false);
        orderRepo.save(order);
        return order.getCost();
    }

    private OrderServiceImpl checkDateOrder(Order order) {
        LocalTime current = LocalTime.now();
        LocalTime start = order.getStartTime();
        LocalTime end = order.getEndTime();
        if (current.compareTo(end) < 0 && current.compareTo(start) > 0) {
            order.setEndTime(LocalTime.now());
        } else if (current.compareTo(end) >= 0)
            log.info(String.format(REMARK_FOR_BOX, order.getBox().getId()));
        else
            throw new DateTimeException(String.format(EXCEPTION_TIME, start.toString()));
        return this;
    }

    private OrderServiceImpl checkDiscountOrder(Order order, Integer discount, User user) {
        if (Objects.nonNull(discount)) {
            Employee employee=user.getEmployee();
            if (Objects.nonNull(employee) && Objects.isNull(employee.getDiscountMin())){
                throw new DiscountException(DISCOUNT_NOT_AVAILABLE);
            }
            if (
                    Objects.nonNull(employee) &&
                    (discount<employee.getDiscountMin() || discount>employee.getDiscountMax() )
            ){
                throw new DiscountException(String.format(INVALID_DISCOUNT,
                        employee.getDiscountMin(), employee.getDiscountMax()));
            }
            BigDecimal percent = new BigDecimal(100 - discount);
            BigDecimal updateCost = order.getCost().scaleByPowerOfTen(-2).multiply(percent)
                    .setScale(2, RoundingMode.CEILING);
            order.setCost(updateCost);
        }
        return this;
    }

    private OrderServiceImpl checkOrderStatus(Order order) {
        if (order.isDone())
            throw new OrderWasDoneException(DONE_ORDER);
        if (!order.isActive())
            throw new OrderWasCanceledException(CANCELED_ORDER);
        return this;
    }
}