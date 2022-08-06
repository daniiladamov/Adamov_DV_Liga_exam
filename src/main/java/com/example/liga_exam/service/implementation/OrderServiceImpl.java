package com.example.liga_exam.service.implementation;

import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.exception.OrderWasCanceledException;
import com.example.liga_exam.exception.OrderWasDoneException;
import com.example.liga_exam.repository.BoxRepo;
import com.example.liga_exam.repository.OrderRepo;
import com.example.liga_exam.service.BoxService;
import com.example.liga_exam.service.OrderService;
import com.example.liga_exam.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final BoxRepo boxRepo;
    @Value("${exception_message}")
    private String exceptionMessage;

    @Transactional
    @Override
    public Long createOrder(Order order, Set<Operation> operations, User user) {
        int duration = operations.stream().mapToInt(o -> o.getDuration()).sum();
        LocalTime startTime = order.getStartTime();
        List<Box> freeBoxes = boxRepo.getFreeBoxes(order.getDate(), startTime.getHour(), startTime.getMinute(), duration);
        Collections.shuffle(freeBoxes);
        order.setBox(freeBoxes.get(0));
        order.setUser(user);
        double v = duration * freeBoxes.get(0).getRatio();
        LocalTime endTime = order.getStartTime().plusMinutes((long) Math.ceil(v));
        order.setEndTime(endTime);
        return orderRepo.save(order).getId();
    }
    @Override
    public Page<Order> getOrders(OrderSearch orderSearch,Pageable pageable, BoxService boxService) {
        OrderSpecification orderSpecification=new OrderSpecification(orderSearch,boxService);
        return orderRepo.findAll(Specification.where(orderSpecification), pageable);

    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Order order=getOrder(id);
        if (order.isDone())
            throw new OrderWasDoneException();
        if (!order.isActive())
            throw new OrderWasCanceledException();
        else{
            order.setActive(false);
            orderRepo.save(order);
        }
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepo.findById(id).orElseThrow(()->
                new EntityNotFoundException(exceptionMessage+id));
    }

}
