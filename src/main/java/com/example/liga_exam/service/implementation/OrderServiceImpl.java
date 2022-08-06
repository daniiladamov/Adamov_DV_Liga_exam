package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.BoxRepo;
import com.example.liga_exam.repository.OrderRepo;
import com.example.liga_exam.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final BoxRepo boxRepo;

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


}
