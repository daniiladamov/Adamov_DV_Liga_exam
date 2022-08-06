package com.example.liga_exam.service;

import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.User;

import java.util.Set;

public interface OrderService {
    Long createOrder(Order order, Set<Operation> operations, User user);
}
