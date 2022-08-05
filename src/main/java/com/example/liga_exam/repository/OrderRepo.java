package com.example.liga_exam.repository;

import com.example.liga_exam.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order,Long> {
    List<Order> findByDate(LocalDate localDate);
}
