package com.example.liga_exam.security;

import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.Service;
import com.example.liga_exam.repository.OrderRepo;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;

    public void createOrder(List<Service> serviceList, LocalTime time, LocalDate localDate){
        List<Order> orders=orderRepo.findByDate(localDate);
    }
}
