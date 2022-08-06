package com.example.liga_exam.repository;

import com.example.liga_exam.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order,Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByDate(LocalDate localDate);
}
