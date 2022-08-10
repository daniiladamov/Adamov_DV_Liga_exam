package com.example.liga_exam.util;

import com.example.liga_exam.entity.*;
import com.example.liga_exam.exception.*;
import com.example.liga_exam.repository.BoxRepo;
import com.example.liga_exam.repository.OrderRepo;
import com.example.liga_exam.security.RoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.example.liga_exam.util.ExceptionMessage.*;


@Component
@Slf4j
@RequiredArgsConstructor
public class OrdersUtil {
    private final BoxRepo boxRepo;
    private final OrderRepo orderRepo;
    @Value("${time_period}")
    private Long timeInterval;
    @Value("${only_weekday}")
    private boolean onlyWeekday;
    @Value("${access_day_interval}")
    private Integer dayInterval;
    @Value("${access_arrived_time}")
    private Integer accessArrivedTime;

    public OrdersUtil setFreeBox(Order order, Set<Operation> operations, User user) {
        int duration = operations.stream().mapToInt(o -> o.getDuration()).sum();
        LocalTime startTime = order.getStartTime();
        List<Box> openBoxes=boxRepo.getOpenBoxesWithEmployee(startTime.getHour(),
                startTime.getMinute(), duration);
        if (openBoxes.isEmpty())
            throw new FreeBoxesNotFound(String.format(NOT_WORKED_BOXES.getMessage(),startTime));
        checkIntersection(order.getDate(),startTime,duration,user.getId());
        List<Box> busyBoxes = boxRepo.getBusyBoxes(order.getDate(),
                startTime.getHour(), startTime.getMinute(), duration);
        if (openBoxes.isEmpty())
            throw new FreeBoxesNotFound(NOT_FOUND_FREE_BOXES.getMessage());
        if (!busyBoxes.isEmpty())
            openBoxes.removeAll(busyBoxes);
        checkAvailableBoxes(openBoxes, order,duration);
        return this;
    }

    private void checkAvailableBoxes(List<Box> openBoxes, Order order, int duration){
        if (openBoxes.isEmpty())
            throw new FreeBoxesNotFound(NOT_FOUND_FREE_BOXES.getMessage());
        Collections.shuffle(openBoxes);
        order.setBox(openBoxes.get(0));
        double calculate = duration * openBoxes.get(0).getRatio();
        LocalTime endTime = order.getStartTime().plusMinutes((long) Math.ceil(calculate));
        order.setEndTime(endTime);
    }
    private void checkIntersection(LocalDate date, LocalTime startTime, int duration, Long id){
        List<Order> getUserOrder=orderRepo.getOrderBetweenTime(date, startTime.getHour(),
                startTime.getMinute(), duration, id);
        if (getUserOrder.size()==1)
            throw new IntersectionOrderTimeException(String.format(INTERSECTION_ORDER_TIME.getMessage(),
                    id,getUserOrder.get(0).getId()));
    }

    public OrdersUtil checkOrderDataTime(Order order) {
        order.setStartTime(Utils.roundTime(order.getStartTime()));
        LocalDate orderDate=order.getDate();
        LocalTime orderTime=order.getStartTime();
        if (LocalDate.now().isAfter(orderDate))
            throw new DateTimeException(INVALID_ORDER_DATE.getMessage());
        else if (LocalDate.now().isEqual(orderDate) &&
                LocalTime.now().isAfter(orderTime.minusMinutes(15L))){
            throw new DateTimeException(String.format(INVALID_ORDER_TIME.getMessage(),
                    timeInterval));
        }
        else if (orderDate.isAfter(LocalDate.now().plusDays(dayInterval))) {
            throw new DateTimeException(String.format(INVALID_DAY_ORDER.getMessage(),
                    dayInterval));
        }
        if (onlyWeekday){
            if (orderDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) ||
                    orderDate.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                throw new FreeBoxesNotFound(NOT_WORK_IN_WEEKENDS.getMessage());
        }
        return this;
    }

    public OrdersUtil setCost(Order order, Set<Operation> operations) {
        BigDecimal cost = operations.stream().map(op -> op.getCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
        cost.setScale(2, RoundingMode.CEILING);
        order.setCost(cost);
        return this;
    }

    public OrdersUtil checkDateOrderDone(Order order) {
        LocalTime current = LocalTime.now();
        LocalTime start = order.getStartTime();
        LocalTime end = order.getEndTime();
        LocalDate date=order.getDate();
        if (current.isBefore(end) && current.isAfter(start) && LocalDate.now().isEqual(date))
            order.setEndTime(LocalTime.now());
        else if (current.isAfter(end) && LocalDate.now().isEqual(date))
            log.info(String.format(REMARK_FOR_BOX.getMessage(), order.getBox().getId()));
        else
            throw new DateTimeException(String.format(EXCEPTION_TIME.getMessage(), start.toString()));
        return this;
    }

    public OrdersUtil checkDiscountOrder(Integer discount, User user) {
        if (Objects.nonNull(discount)) {
            Employee employee = user.getEmployee();
            if (Objects.nonNull(employee) && Objects.isNull(employee.getDiscountMin())) {
                throw new DiscountException(DISCOUNT_NOT_AVAILABLE.getMessage());
            }
            if (
                    Objects.nonNull(employee) &&
                            (discount < employee.getDiscountMin() || discount > employee.getDiscountMax())){
                throw new DiscountException(String.format(INVALID_DISCOUNT.getMessage(),
                        employee.getDiscountMin(), employee.getDiscountMax()));
            }
        }
        return this;
    }

    public OrdersUtil checkOrderStatus(Order order) {
        if (order.getOrderStatus().equals(OrderStatus.DONE))
            throw new OrderWasDoneException(DONE_ORDER.getMessage());
        if (order.getOrderStatus().equals(OrderStatus.CANCELED))
            throw new OrderWasCanceledException(CANCELED_ORDER.getMessage());
        if (order.getOrderStatus().equals(OrderStatus.ACTIVE_ARRIVED))
            throw new RepeatedArrivedException(REPEATED_ARRIVED.getMessage());
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

    public OrdersUtil checkTimeArrived(Order order) {
        if (LocalDate.now().isEqual(order.getDate()) &&
        LocalTime.now().isAfter(order.getStartTime().minusMinutes(accessArrivedTime)) &&
        LocalTime.now().isBefore(order.getStartTime()))
            return this;
        else
            throw new DateTimeException(String.format(INVALID_ARRIVED_TIME.getMessage(),
                    accessArrivedTime));
    }
    public OrdersUtil checkOrderConform(Order order){
        if (order.getConfirm())
            throw new OrderConfirmException(REPEAT_CONFIRM.getMessage());
        return this;
    }
}
