package com.example.liga_exam.util;

import com.example.liga_exam.entity.*;
import com.example.liga_exam.exception.*;
import com.example.liga_exam.repository.BoxRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrdersUtil {
    private final BoxRepo boxRepo;
    @Value("${time_period}")
    private Long timeInterval;

    public static String REMARK_FOR_BOX = "В Box#%d услуги выполняются с отклонением от графика";
    public static String EXCEPTION_TIME = "Нельзя завершать услугу до времени ее начала - %s";
    public static String CANCELED_ORDER = "Заказ был отменен ранее, операция недоступна";
    public static String DONE_ORDER = "Заказ был выполнен ранее, операция недоступна";
    public static String INVALID_INTERVAL = "Временной интервал задан неверно";
    public static String INVALID_DISCOUNT = "Можно назначать скидку в пределах %d% - %d%";
    public static String DISCOUNT_NOT_AVAILABLE = "Работнику запрещено назначать скидку";
    public static String INVALID_ORDER_DATE = "Запись на прошедшие даты не доступна";
    public static String INVALID_ORDER_TIME = "Запись допступна минимум на %d минут более текущего времени";
    public static String NOT_FOUND_FREE_BOXES = "Нет свободных мест на выбранные дату и время";
    public static String REPEATED_ARRIVED = "Отметка о присутсвии уже выставлена ранее";

    public OrdersUtil setFreeBox(Order order, Set<Operation> operations) {
        int duration = operations.stream().mapToInt(o -> o.getDuration()).sum();
        LocalTime startTime = order.getStartTime();
        List<Box> freeBoxes = boxRepo.getFreeBoxes(order.getDate(),
                startTime.getHour(), startTime.getMinute(), duration);
        if (freeBoxes.isEmpty())
            throw new FreeBoxesNotFound(NOT_FOUND_FREE_BOXES);
        Collections.shuffle(freeBoxes);
        order.setBox(freeBoxes.get(0));
        double calculate = duration * freeBoxes.get(0).getRatio();
        LocalTime endTime = order.getStartTime().plusMinutes((long) Math.ceil(calculate));
        order.setEndTime(endTime);
        return this;
    }

    public OrdersUtil checkOrderDataTime(Order order) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        if (currentDate.compareTo(order.getDate()) > 0)
            throw new DateTimeException(INVALID_ORDER_DATE);
        else if (currentTime.compareTo(order.getStartTime().minusMinutes(15L)) < 0)
            throw new DateTimeException(String.format(INVALID_ORDER_TIME, timeInterval));
        LocalTime roundTime = Utils.roundTime(order.getStartTime());
        order.setStartTime(roundTime);
        return this;
    }

    public OrdersUtil setCost(Order order, Set<Operation> operations) {
        BigDecimal cost = operations.stream().map(op -> op.getCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
        cost.setScale(2, RoundingMode.CEILING);
        order.setCost(cost);
        return this;
    }

    public OrdersUtil checkDateOrder(Order order) {
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

    public OrdersUtil checkDiscountOrder(Order order, Integer discount, User user) {
        if (Objects.nonNull(discount)) {
            Employee employee = user.getEmployee();
            if (Objects.nonNull(employee) && Objects.isNull(employee.getDiscountMin())) {
                throw new DiscountException(DISCOUNT_NOT_AVAILABLE);
            }
            if (
                    Objects.nonNull(employee) &&
                            (discount < employee.getDiscountMin() || discount > employee.getDiscountMax())
            ) {
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

    public OrdersUtil checkOrderStatus(Order order) {
        if (order.getOrderStatus().equals(OrderStatus.DONE))
            throw new OrderWasDoneException(DONE_ORDER);
        if (order.getOrderStatus().equals(OrderStatus.CANCELED))
            throw new OrderWasCanceledException(CANCELED_ORDER);
        if (order.getOrderStatus().equals(OrderStatus.ACTIVE_ARRIVED))
            throw new RepeatedArrivedException(REPEATED_ARRIVED);
        return this;
    }

    public OrdersUtil checkAccess(Order order, User user) throws AuthenticationException {
        if (user.getRole().equals(RoleEnum.ROLE_ADMIN))
            return this;
        Employee employee = user.getEmployee();
        if (Objects.nonNull(employee) && !order.getBox().getEmployees().contains(employee)) {
            throw new AuthenticationException();
        }
        if (Objects.isNull(employee) && order.getUser().getId()!= user.getId()){
            throw new AuthenticationException();
        }
        return this;
    }
}
