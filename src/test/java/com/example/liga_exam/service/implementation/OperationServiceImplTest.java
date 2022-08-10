package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.Operation;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.OperationRepo;
import com.example.liga_exam.service.OperationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OperationServiceImplTest {
    @Mock
    private OperationRepo operationRepo;
    private OperationService operationService;
    private Operation operation;
    private Long id=1L;
    Set<Long> setOperId;
    Pageable pageable;
    Page<Operation> page;

    public OperationServiceImplTest() {
        MockitoAnnotations.openMocks(this);
        operationService=new OperationServiceImpl(operationRepo);
        operation=new Operation();
        operation.setId(id);
        setOperId=Set.of(id);
        pageable= PageRequest.of(0,5);
        page=new PageImpl<>(List.of(operation));
    }

    @Test
    void createService_ExpectedBehavior() {
        Mockito.when(operationRepo.save(operation)).thenReturn(operation);
        Long operId = operationService.createService(operation);
        Mockito.verify(operationRepo,Mockito.times(1)).save(operation);
        Assertions.assertEquals(operation.getId(),operId);
    }

    @Test
    void getOperations_ReturnOperationList_ExpectedBehavior() {
        Mockito.when(operationRepo.findAllById(setOperId)).thenReturn(List.of(operation));
        Set<Operation> operations = operationService.getOperations(setOperId);
        Mockito.verify(operationRepo,Mockito.times(1)).findAllById(setOperId);
        Assertions.assertEquals(operations,Set.of(operation));
    }

    @Test
    void getOperations_OperationsNotFound_ExpectedBehavior() {
        Mockito.when(operationRepo.findAllById(setOperId)).thenReturn(Collections.emptyList());
        Throwable throwable=Assertions.assertThrows(EntityNotFoundException.class,()->
                operationService.getOperations(setOperId));
        Assertions.assertNotNull(throwable);
        Mockito.verify(operationRepo,Mockito.times(1)).findAllById(setOperId);
    }

    @Test
    void GetOperations_PageRequest_ExpectedBehavior() {
        Mockito.when(operationRepo.findAll(pageable)).thenReturn(page);
        Page<Operation> operations = operationService.getOperations(pageable);
        Mockito.verify(operationRepo,Mockito.times(1)).findAll(pageable);
        Assertions.assertEquals(page,operations);
    }
    @Test
    void GetOperations_OperationsNotFound_ExpectedBehavior() {
        Mockito.when(operationRepo.findAll(pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));
        Throwable throwable=Assertions.assertThrows(EntityNotFoundException.class,()->
                operationService.getOperations(pageable));
        Assertions.assertNotNull(throwable);
        Mockito.verify(operationRepo,Mockito.times(1)).findAll(pageable);

    }
}