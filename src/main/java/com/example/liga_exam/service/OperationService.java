package com.example.liga_exam.service;

import com.example.liga_exam.entity.Operation;

import java.util.Set;


public interface OperationService {
    Long createService(Operation service);
    Set<Operation> getOperations(Set<Long> ids);
}
