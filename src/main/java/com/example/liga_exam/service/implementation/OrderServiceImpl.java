package com.example.liga_exam.service.implementation;

import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.OrderStatus;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.exception.FreeBoxesNotFound;
import com.example.liga_exam.repository.OrderRepo;
import com.example.liga_exam.service.BoxService;
import com.example.liga_exam.service.OrderService;
import com.example.liga_exam.specification.OrderSpecification;
import com.example.liga_exam.util.OrdersUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.example.liga_exam.util.OrdersUtil.INVALID_INTERVAL;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    @Value("${exception_message}")
    private String exceptionMessage;
    private final OrdersUtil ordersUtil;

    @Transactional
    @Override
    public Long createOrder(Order order, Set<Operation> operations, User user) {
        ordersUtil
                .checkOrderDataTime(order)
                .setFreeBox(order, operations)
                .setCost(order, operations);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.ACTIVE);
        order.setOperations(operations);
        return orderRepo.save(order).getId();
    }

    @Override
    public Page<Order> getOrders(OrderSearch orderSearch, Pageable pageable, BoxService boxService) {
        OrderSpecification orderSpecification = new OrderSpecification(orderSearch, boxService);
        return orderRepo.findAll(Specification.where(orderSpecification), pageable);
    }

    @Override
    public Page<Order> getOrders(Specification<Order> specification, Pageable pageable) {
        return orderRepo.findAll(Specification.where(specification), pageable);
    }

    @Override
    @Transactional(rollbackFor = FreeBoxesNotFound.class)
    public void updateOrder(Long id, Order updatedOrder, Set<Operation> operations, User user) {
        Order order = getOrder(id);
        ordersUtil.checkOrderStatus(order);
        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepo.save(order);
        order.setStartTime(updatedOrder.getStartTime());
        order.setDate(updatedOrder.getDate());
        createOrder(order, operations, user);
    }

    @Override
    @Transactional
    public void arrived(Long id) {
        Order order = getOrder(id);
        ordersUtil.checkOrderStatus(order);
        order.setOrderStatus(OrderStatus.ACTIVE_ARRIVED);
        orderRepo.save(order);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Order order = getOrder(id);
        ordersUtil.checkOrderStatus(order);
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
        ordersUtil
                .checkOrderStatus(order)
                .checkDateOrder(order)
                .checkDiscountOrder(order, discount, user);
        order.setOrderStatus(OrderStatus.DONE);
        orderRepo.save(order);
        return order.getCost();
    }
}
