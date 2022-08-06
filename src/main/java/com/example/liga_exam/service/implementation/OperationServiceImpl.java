package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.repository.OperationRepo;
import com.example.liga_exam.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationServiceImpl implements OperationService {
    private final OperationRepo operationRepo;

    @Override
    @Transactional
    public Long createService(Operation service) {
        return operationRepo.save(service).getId();
    }

    @Override
    public Set<Operation> getOperations(Set<Long> ids) {
        return new HashSet<>(operationRepo.findAllById(ids));
    }
}
