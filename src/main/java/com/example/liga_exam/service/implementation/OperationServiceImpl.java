package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.OperationRepo;
import com.example.liga_exam.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationServiceImpl implements OperationService {
    private final OperationRepo operationRepo;
    private final static String NOT_FOUND_OPERATION = "Указаны несуществующие коды операций";

    @Override
    @Transactional
    public Long createService(Operation service) {
        return operationRepo.save(service).getId();
    }

    @Override
    public Set<Operation> getOperations(Set<Long> ids) {
        Set<Operation> set = new HashSet<>(operationRepo.findAllById(ids));
        if (set.isEmpty())
            throw new EntityNotFoundException(NOT_FOUND_OPERATION);
        return set;
    }

    @Override
    public Page<Operation> getOperations(Pageable pageable) {
        Page<Operation> page = operationRepo.findAll(pageable);
        if (page.getContent().isEmpty())
            throw new EntityNotFoundException(NOT_FOUND_OPERATION);
        return page;
    }
}
