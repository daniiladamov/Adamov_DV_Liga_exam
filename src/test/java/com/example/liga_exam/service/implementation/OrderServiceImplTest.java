package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.*;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.exception.FreeBoxesNotFound;
import com.example.liga_exam.exception.OrderConfirmException;
import com.example.liga_exam.repository.OrderRepo;
import com.example.liga_exam.service.OrderService;
import com.example.liga_exam.specification.OrderSpecification;
import com.example.liga_exam.util.OrdersUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.example.liga_exam.entity.OrderStatus.*;

class OrderServiceImplTest {
    @Mock
    private OrderRepo orderRepo;
    @Mock
    private OrdersUtil ordersUtil;
    private OrderService orderService;
    private User user;
    private Box box;
    private Order order;
    private Order createdOrder;
    private Long id = 1L;
    private Pageable pageable;
    private BigDecimal cost = new BigDecimal(100).setScale(2, RoundingMode.CEILING);
    private Operation operation;
    private Set<Operation> operations;
    private Employee employee;
    private Specification<Order> specification;

    public OrderServiceImplTest() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderServiceImpl(orderRepo, ordersUtil);
        ((OrderServiceImpl)orderService).setTimeForConfirm(1L);
        pageable = PageRequest.of(0, 5);
    }

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setId(id);
        box = new Box();
        box.setId(id);
        order = new Order();
        order.setId(id);
        specification = new OrderSpecification(null, box, user);
        employee = new Employee();
        employee.setId(id);
        employee.setBox(box);
        operation = new Operation();
        operation.setId(id);
        operation.setCost(cost);
        order.setOrderStatus(OrderStatus.ACTIVE);
        order.setCost(cost);
        order.setUser(user);
        operations=Set.of(operation);
    }

    @Test
    void GetOrders_ExpectedBehavior() {
        Mockito.when(orderRepo.findAll(specification, pageable)).thenReturn(new PageImpl<>(List.of(order)));
        Page<Order> orders = orderService.getOrders(specification, pageable);
        Mockito.verify(orderRepo, Mockito.times(1)).findAll(specification, pageable);
        Assertions.assertEquals(1, orders.getTotalElements());
        Assertions.assertEquals(id, orders.getContent().get(0).getId());
    }

    @Test
    void GetOrders_OrdersNotFound_ExpectedBehavior() {
        Mockito.when(orderRepo.findAll(specification, pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));
        Page<Order> orders = orderService.getOrders(specification, pageable);
        Mockito.verify(orderRepo, Mockito.times(1)).findAll(specification, pageable);
        Assertions.assertEquals(0, orders.getTotalElements());
    }

    @Test
    void updateOrder_NoFreeBoxesRollback_ExpectedBehavior() throws AuthenticationException, InterruptedException {
        Order updatedOrder=new Order();
        updatedOrder.setStartTime(LocalTime.now().plusMinutes(10));
        updatedOrder.setDate(LocalDate.now().plusDays(1));
        updatedOrder.setOperations(Set.of(new Operation(),operation));

        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(order));
        Mockito.when(orderRepo.save(order)).thenReturn(order);
        Mockito.when(ordersUtil.checkAccess(order, user)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.checkOrderStatus(order)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.checkOrderDataTime(order)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.setFreeBox(order, operations,user)).thenThrow(FreeBoxesNotFound.class);

        Throwable throwable=Assertions.assertThrows(FreeBoxesNotFound.class,()->
                orderService.updateOrder(id,updatedOrder,operations,user));
        Assertions.assertNotNull(throwable);
        Mockito.verify(ordersUtil,Mockito.times(1)).checkAccess(order, user);
        Mockito.verify(ordersUtil,Mockito.times(1)).checkOrderStatus(order);
        Mockito.verify(ordersUtil,Mockito.times(1)).checkOrderDataTime(order);
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
        Mockito.verify(orderRepo, Mockito.times(1)).save(order);
    }
    @Test
    void updateOrder_WithCancelOrderAfterOneMinute_ExpectedBehavior() throws AuthenticationException, InterruptedException {
        Order updatedOrder=new Order();
        updatedOrder.setStartTime(LocalTime.now());
        updatedOrder.setDate(LocalDate.now());
        updatedOrder.setOperations(operations);

        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(order));
        Mockito.when(orderRepo.save(order)).thenReturn(order);
        Mockito.when(ordersUtil.checkAccess(order, user)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.checkOrderStatus(order)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.checkOrderDataTime(order)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.setCost(order, operations)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.setFreeBox(order, operations,user)).thenReturn(ordersUtil);

        orderService.updateOrder(id,updatedOrder,operations,user);

        Assertions.assertEquals(ACTIVE,order.getOrderStatus());
        Assertions.assertFalse(order.getConfirm());
        Assertions.assertEquals(Set.of(operation),order.getOperations());

        Mockito.verify(ordersUtil,Mockito.times(1)).checkAccess(order, user);
        Mockito.verify(ordersUtil,Mockito.times(1)).checkOrderStatus(order);
        Mockito.verify(ordersUtil,Mockito.times(1)).checkOrderDataTime(order);
        Mockito.verify(ordersUtil,Mockito.times(1)).setCost(order, operations);

        TimeUnit.MINUTES.sleep(1);
        Assertions.assertEquals(CANCELED,order.getOrderStatus());
        Mockito.verify(orderRepo, Mockito.times(2)).findById(id);
        Mockito.verify(orderRepo, Mockito.times(3)).save(order);
    }


    @Test
    void createOrder_WithCancelOrderAfterOneMinute_ExpectedBehavior()
            throws AuthenticationException, InterruptedException {
        createdOrder=new Order();
        createdOrder.setId(id);
        Mockito.when(orderRepo.save(createdOrder)).thenReturn(createdOrder);
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(createdOrder));
        Mockito.when(ordersUtil.checkOrderDataTime(createdOrder)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.setFreeBox(createdOrder, operations, user)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.setCost(createdOrder, operations)).thenReturn(ordersUtil);

        orderService.createOrder(createdOrder, operations, user);

        Assertions.assertEquals(ACTIVE,createdOrder.getOrderStatus());
        Assertions.assertFalse(createdOrder.getConfirm());
        Assertions.assertEquals(Set.of(operation),createdOrder.getOperations());
        Mockito.verify(ordersUtil,Mockito.times(1)).checkOrderDataTime(createdOrder);
        Mockito.verify(ordersUtil,Mockito.times(1)).setFreeBox(createdOrder, operations, user);
        Mockito.verify(ordersUtil,Mockito.times(1)).setCost(createdOrder, operations);

        TimeUnit.MINUTES.sleep(1);
        Assertions.assertEquals(CANCELED,createdOrder.getOrderStatus());
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
        Mockito.verify(orderRepo, Mockito.times(2)).save(createdOrder);
    }

    @Test
    void arrived_ExpectedBehavior() throws AuthenticationException {
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(order));
        Mockito.when(orderRepo.save(order)).thenReturn(order);
        Mockito.when(ordersUtil.checkAccess(order, user)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.checkTimeArrived(order)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.checkOrderStatus(order)).thenReturn(ordersUtil);

        orderService.arrived(id,user);
        Assertions.assertEquals(ACTIVE_ARRIVED,order.getOrderStatus());

        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
        Mockito.verify(orderRepo, Mockito.times(1)).save(order);
        Mockito.verify(ordersUtil,Mockito.times(1)).checkAccess(order, user);
        Mockito.verify(ordersUtil,Mockito.times(1)).checkTimeArrived(order);
        Mockito.verify(ordersUtil,Mockito.times(1)).checkOrderStatus(order);
    }
    @Test
    void arrived_OrderNotFound_ExpectedBehavior() {
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(null));
        Mockito.when(orderRepo.save(order)).thenReturn(order);
        Throwable throwable = Assertions.assertThrows(EntityNotFoundException.class, () ->
                orderService.arrived(id,user));
        Assertions.assertNotNull(throwable);
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
        Mockito.verify(orderRepo, Mockito.times(0)).save(order);
    }

    @Test
    void confirmOrder_ExpectedBehavior() {
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(order));
        Mockito.when(orderRepo.save(order)).thenReturn(order);
        Mockito.when(ordersUtil.checkOrderConform(order)).thenReturn(ordersUtil);
        orderService.confirmOrder(id);
        Assertions.assertTrue(order.getConfirm());
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
        Mockito.verify(orderRepo, Mockito.times(1)).save(order);
    }

    @Test
    void confirmOrder_OrderNotFound_ExpectedBehavior() {
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(null));
        Throwable throwable = Assertions.assertThrows(EntityNotFoundException.class, () ->
                orderService.confirmOrder(id));
        Assertions.assertNotNull(throwable);
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
    }
    @Test
    void cancelNotConfirmOrder_IfOrderWasConfirm_ExpectedBehavior() {
        order.setConfirm(true);
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(order));
        Mockito.when(orderRepo.save(order)).thenReturn(order);
        orderService.cancelNotConfirmOrder(id);
        Assertions.assertEquals(ACTIVE,order.getOrderStatus());
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
        Mockito.verify(orderRepo, Mockito.times(0)).save(order);
    }

    @Test
    void cancelNotConfirmOrder_ExpectedBehavior() {
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(order));
        Mockito.when(orderRepo.save(order)).thenReturn(order);
        orderService.cancelNotConfirmOrder(id);
        Assertions.assertEquals(CANCELED,order.getOrderStatus());
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
        Mockito.verify(orderRepo, Mockito.times(1)).save(order);
    }
    @Test
    void cancelNotConfirmOrder_OrderNotFound_ExpectedBehavior() {
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(null));
        Throwable throwable = Assertions.assertThrows(EntityNotFoundException.class, () ->
                orderService.cancelNotConfirmOrder(id));
        Assertions.assertNotNull(throwable);
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
    }

    @Test
    void cancel_ExpectedBehavior() throws AuthenticationException {
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(order));
        Mockito.when(orderRepo.save(order)).thenReturn(order);
        Mockito.when(ordersUtil.checkAccess(order, user)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.checkOrderStatus(order)).thenReturn(ordersUtil);
        orderService.cancel(id, user);
        Assertions.assertEquals(CANCELED, order.getOrderStatus());
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
        Mockito.verify(ordersUtil, Mockito.times(1)).checkAccess(order, user);
        Mockito.verify(ordersUtil, Mockito.times(1)).checkOrderStatus(order);
    }

    @Test
    void cancel_OrderNotFound_ExpectedBehavior() {
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(null));
        Throwable throwable = Assertions.assertThrows(EntityNotFoundException.class, () ->
                orderService.cancel(id, user));
        Assertions.assertNotNull(throwable);
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);

    }

    @Test
    void getOrder_ExpectedBehavior() {
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(order));
        Order ordr = orderService.getOrder(id);
        Assertions.assertEquals(order.getId(), ordr.getId());
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
    }

    @Test
    void getOrder_OrderNotFound_ExpectedBehavior() {
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(null));
        Throwable throwable = Assertions.assertThrows(EntityNotFoundException.class, () ->
                orderService.getOrder(id));
        Assertions.assertNotNull(throwable);
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
    }

    @Test
    void getRevenue_ExpectedBehavior() {
        LocalDate from = LocalDate.now().minusDays(1);
        Mockito.when(orderRepo.findAll(Mockito.any(Specification.class))).thenReturn(List.of(order));
        BigDecimal revenue = orderService.getRevenue(from, LocalDate.now());
        Assertions.assertEquals(order.getCost(), revenue);
        Mockito.verify(orderRepo, Mockito.times(1))
                .findAll(Mockito.any(Specification.class));
    }

    @Test
    void getRevenue_OrdersNotFound_ExpectedBehavior() {
        LocalDate from = LocalDate.now().minusDays(1);
        Mockito.when(orderRepo.findAll(Mockito.any(Specification.class))).thenReturn(Collections.emptyList());
        BigDecimal revenue = orderService.getRevenue(from, LocalDate.now());
        Assertions.assertEquals(BigDecimal.ZERO, revenue);
        Mockito.verify(orderRepo, Mockito.times(1))
                .findAll(Mockito.any(Specification.class));
    }

    @Test
    void getRevenue_InvalidDateInterval_ExpectedBehavior() {
        Mockito.when(orderRepo.findAll(Mockito.any(Specification.class))).thenReturn(List.of(order));
        LocalDate from = LocalDate.now().minusDays(1);
        Throwable throwable1 = Assertions.assertThrows(DateTimeException.class, () ->
                orderService.getRevenue(LocalDate.now(), from));
        Throwable throwable2 = Assertions.assertThrows(DateTimeException.class, () ->
                orderService.getRevenue(null, null));
        Assertions.assertNotNull(throwable1);
        Assertions.assertNotNull(throwable2);
        Mockito.verify(orderRepo, Mockito.times(0))
                .findAll(Mockito.any(Specification.class));
    }

    @Test
    void doneOrder_ExpectedBehavior() throws AuthenticationException {
        Integer discount = null;
        Mockito.when(orderRepo.save(order)).thenReturn(order);
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(order));
        Mockito.when(ordersUtil.checkOrderStatus(order)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.checkDateOrderDone(order)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.checkAccess(order, user)).thenReturn(ordersUtil);
        Mockito.when(ordersUtil.checkDiscountOrder(discount, user)).thenReturn(ordersUtil);

        BigDecimal bigDecimal = orderService.doneOrder(id, discount, user);
        Assertions.assertEquals(cost, bigDecimal);

        discount = 5;
        cost = cost.subtract(new BigDecimal(discount));
        bigDecimal = orderService.doneOrder(id, discount, user);
        Mockito.verify(orderRepo, Mockito.times(2)).save(order);
        Assertions.assertEquals(cost, bigDecimal);
    }

    @Test
    void doneOrder_InvalidOrderId_ExpectedBehavior() {
        Mockito.when(orderRepo.findById(id)).thenReturn(Optional.ofNullable(null));
        Throwable throwable = Assertions.assertThrows(EntityNotFoundException.class, () ->
                orderService.doneOrder(id, null, user));
        Assertions.assertNotNull(throwable);
        Mockito.verify(orderRepo, Mockito.times(1)).findById(id);
    }
}