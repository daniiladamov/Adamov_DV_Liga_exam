package com.example.liga_exam.repository;

import com.example.liga_exam.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepo extends JpaRepository<Operation,Long> {
}
