package com.example.liga_exam.service.implementation;

import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.entity.*;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.exception.FreeBoxesNotFound;
import com.example.liga_exam.repository.OrderRepo;
import com.example.liga_exam.service.OrderService;
import com.example.liga_exam.specification.OrderSpecification;
import com.example.liga_exam.util.OrdersUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.example.liga_exam.util.ExceptionMessage.INVALID_INTERVAL;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Getter
@Setter
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final OrdersUtil ordersUtil;
    @Value("${exception_message}")
    private String exceptionMessage;
    @Value("${time_period}")
    private Long timeForConfirm;
    private final static String BASE_URL_CONFIRM="http://localhost:8080/api/orders/%d/confirm";

    @Override
    public Page<Order> getOrders(OrderSearch orderSearch, Pageable pageable, Box box, User user) {
        OrderSpecification orderSpecification = new OrderSpecification(orderSearch, box, user);
        return orderRepo.findAll(Specification.where(orderSpecification), pageable);
    }

    @Override
    public Page<Order> getOrders(Specification<Order> specification, Pageable pageable) {
        return orderRepo.findAll(Specification.where(specification), pageable);
    }

    @Override
    @Transactional(rollbackFor = FreeBoxesNotFound.class)
    public String updateOrder(Long id, Order updatedOrder, Set<Operation> operations, User user)
            throws AuthenticationException {
        Order order = getOrder(id);
        ordersUtil
                .checkAccess(order, user)
                .checkOrderStatus(order);
        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepo.save(order);
        order.setStartTime(updatedOrder.getStartTime());
        order.setConfirm(false);
        order.setDate(updatedOrder.getDate());
        return createOrder(order, operations, order.getUser());
    }

    @Override
    @Transactional
    public void arrived(Long id, User user) throws AuthenticationException {
        Order order = getOrder(id);
        ordersUtil
                .checkAccess(order, user)
                .checkOrderStatus(order)
                .checkTimeArrived(order);
        order.setOrderStatus(OrderStatus.ACTIVE_ARRIVED);
        orderRepo.save(order);
    }

    @Transactional
    @Override
    public String createOrder(Order order, Set<Operation> operations, User user) {
        ordersUtil
                .checkOrderDataTime(order)
                .setFreeBox(order, operations, user)
                .setCost(order, operations);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.ACTIVE);
        order.setOperations(operations);
        Order saveOrder = orderRepo.save(order);
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(timeForConfirm);
                cancelNotConfirmOrder(saveOrder.getId());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        return String.format(BASE_URL_CONFIRM, saveOrder.getId());
    }

    @Override
    @Transactional
    public Long confirmOrder(Long id) {
        Order order = getOrder(id);
        ordersUtil.checkOrderConform(order);
        order.setOrderStatus(OrderStatus.ACTIVE);
        order.setConfirm(true);
        return orderRepo.save(order).getId();
    }

    @Override
    public void cancelNotConfirmOrder(Long id) {
        Order order = getOrder(id);
        if (!order.getConfirm()) {
            order.setOrderStatus(OrderStatus.CANCELED);
            orderRepo.save(order);
        }
    }

    @Override
    @Transactional
    public void cancel(Long id, User user) throws AuthenticationException {
        Order order = getOrder(id);
        ordersUtil
                .checkAccess(order, user)
                .checkOrderStatus(order);
        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepo.save(order);
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException(exceptionMessage + id));
    }

    @Override
    public BigDecimal getRevenue(LocalDate fromDate, LocalDate toDate) {
        if ((Objects.isNull(fromDate) && Objects.isNull(toDate))
                || fromDate.isAfter(toDate)) {
            throw new DateTimeException(INVALID_INTERVAL.getMessage());
        }
        List<Order> revenue = orderRepo.findAll(Specification.where(
                OrderSpecification.revenuePredicate(fromDate, toDate)));
        return revenue.stream().map(Order::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public BigDecimal doneOrder(Long orderId, Integer discount, User user)
            throws AuthenticationException {
        Order order = getOrder(orderId);
        ordersUtil
                .checkOrderStatus(order)
                .checkDateOrderDone(order)
                .checkAccess(order, user)
                .checkDiscountOrder(discount, user);
        BigDecimal percent = new BigDecimal(100);
        if (Objects.nonNull(discount))
            percent = new BigDecimal(100 - discount);
        BigDecimal updateCost = order.getCost().scaleByPowerOfTen(-2).multiply(percent)
                .setScale(2, RoundingMode.CEILING);
        order.setCost(updateCost);
        order.setOrderStatus(OrderStatus.DONE);
        orderRepo.save(order);
        return order.getCost();
    }
}
