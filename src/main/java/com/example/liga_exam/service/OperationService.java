package com.example.liga_exam.service;

import com.example.liga_exam.entity.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;


public interface OperationService {
    Long createService(Operation service);

    Set<Operation> getOperations(Set<Long> ids);

    Page<Operation> getOperations(Pageable pageable);
}
