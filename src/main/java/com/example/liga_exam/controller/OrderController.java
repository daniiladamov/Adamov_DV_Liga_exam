package com.example.liga_exam.controller;

import com.example.liga_exam.dto.request.OrderReqDto;
import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.dto.request.PeriodDto;
import com.example.liga_exam.dto.response.OrderResDto;
import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.mapper.OrderMapper;
import com.example.liga_exam.service.*;
import com.example.liga_exam.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/orders")
public class OrderController {
    private final OperationService operationService;
    private final OrderService orderService;
    private final UserService userService;
    private final BoxService boxService;
    private final OrderMapper orderMapper;
    private final AuthService authService;
    private final static String CONFIRM_REGISTRATION = "Запись подтверждена, номер заказа id=";

    /**
     * Создание заказа
     * @param orderReqDto модель заказа
     * @return ссылку на подтверждение брони заказа
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public String createOrder(@Validated @RequestBody OrderReqDto orderReqDto) {
        Set<Operation> operationSet = operationService.getOperations(
                orderReqDto.getServices().stream().map(o -> o.getId()).collect(Collectors.toSet()));
        Order order = orderMapper.toEntity(orderReqDto);
        User user = userService.getUserByUsername(authService.getUsername());
        return orderService.createOrder(order, operationSet, user);
    }

    /**
     * Подтверждение брони
     * @param id номер заказа
     * @return ссылку для подтверждения брони заказа
     */
    @GetMapping("/{id}/confirm")
    public String confirmRegistration(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return CONFIRM_REGISTRATION + id;
    }

    /**
     * Просмотр заказов
     * @param pageSize размер страницы
     * @param pageNumber номер страницы
     * @param orderSearch модель фильтрации данных
     * @return страницу с информацией о заказах
     */
    @PostMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public Page<OrderResDto> getOrders(Integer pageSize, Integer pageNumber,
                                       @Validated @RequestBody OrderSearch orderSearch) {
        Box box = boxService.getBox(orderSearch.getBoxId());
        User user = userService.getUserByUsername(authService.getUsername());
        Pageable pageable = Utils.getPageable(pageNumber, pageSize);
        return orderService.getOrders(orderSearch, pageable, box, user)
                .map(x -> orderMapper.toResponse(x));
    }

    /**
     * Выручка предприятия
     * @param dto моедль периода времени
     * @return выручку предприятия за указанный период
     */
    @PostMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public BigDecimal getRevenue(@RequestBody PeriodDto dto) {
        return orderService.getRevenue(dto.getFromDate(), dto.getToDate());
    }

    /**
     * Подтверждение заблаговременного приезда клиента
     * @param id номер заказа
     * @throws AuthenticationException если приезд подтверждает кто-либо кроме самого пользователя, сделавшего заказ,
     * работинка бокса, где оказывается услуга, или администратора
     */
    @PatchMapping("/{id}/customer-arrived")
    public void customerArrivedInTime(@PathVariable Long id)
            throws AuthenticationException {
        User user = userService.getUserByUsername(authService.getUsername());
        orderService.arrived(id, user);
    }

    /**
     * Отмена заказа
     * @param id номер заказа
     * @throws AuthenticationException если отмену выполняет кто-либо кроме самого пользователя, сделавшего заказ,
     * работинка бокса, где оказывается услуга, или администратора
     */
    @PatchMapping("/{id}/cancel-order")
    public void canceledOrder(@PathVariable Long id) throws AuthenticationException {
        User user = userService.getUserByUsername(authService.getUsername());
        orderService.cancel(id, user);
    }

    /**
     * Изменение параметров заказа
     * @param id номер заказа
     * @param dto модель обновленной информации по заказу
     * @return ссылку на подтверждение брони заказа
     * @throws AuthenticationException если изменение заказа выполняет кто-либо кроме самого пользователя,
     * сделавшего заказ, работинка бокса, где оказывается услуга, или администратора
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','USER')")
    public String changeOrder(@PathVariable Long id, @Validated @RequestBody OrderReqDto dto)
            throws AuthenticationException {
        User user = userService.getUserByUsername(authService.getUsername());
        Set<Operation> operationSet = operationService.getOperations(
                dto.getServices().stream().map(o -> o.getId()).collect(Collectors.toSet()));
        return orderService.updateOrder(id, orderMapper.toEntity(dto), operationSet, user);
    }

    /**
     * Завершение заказа
     * @param id номер заказа
     * @param discount размер скидки (в %)
     * @return итоговую стоимость заказа
     * @throws AuthenticationException если завершение заказа выполняет кто-либо кром работинка бокса, где оказывается
     * услуга, или администратора
     */
    @PatchMapping("/{id}/done-order")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public BigDecimal doneOrder(@PathVariable Long id, Integer discount)
            throws AuthenticationException {
        User user = userService.getUserByUsername(authService.getUsername());
        return orderService.doneOrder(id, discount, user);
    }
}
