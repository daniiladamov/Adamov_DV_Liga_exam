package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.Box;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.BoxRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BoxServicesImplTest {
    @Mock
    private BoxRepo boxRepo;
    private BoxServicesImpl boxServices;
    private Long boxIdCreated=1L;
    private Long boxIdUpdated=2L;
    private Box boxCreated=new Box();
    private Box boxUpdated=new Box();
    @Value("${exception_message}")
    private String exceptionMessage;

    public BoxServicesImplTest() {
        MockitoAnnotations.openMocks(this);
        boxServices=new BoxServicesImpl(boxRepo);
        boxCreated.setId(boxIdCreated);
        boxCreated.setOpen(LocalTime.MIN);
        boxCreated.setClose(LocalTime.MAX);
        boxServices.setCompanyClose(22);
        boxServices.setCompanyOpen(8);
        boxUpdated.setId(boxIdUpdated);
        boxUpdated.setOpen(LocalTime.MIN);
        boxUpdated.setClose(LocalTime.MAX);
    }

    @Test
    void createBox_ExpectedBehavior() {
        Mockito.when(boxRepo.save(boxCreated)).thenReturn(boxCreated);
        Mockito.when(boxRepo.save(boxCreated)).thenReturn(boxCreated);
        Long boxId = boxServices.createBox(boxCreated);
        Mockito.verify(boxRepo,Mockito.times(1)).save(boxCreated);
        Assertions.assertEquals(boxId,boxCreated.getId());
    }

    @Test
    void getBox_findBox_ExpectedBehavior() {
        Mockito.when(boxRepo.findById(boxIdCreated)).thenReturn(Optional.ofNullable(boxCreated));
        Box box = boxServices.getBox(boxIdCreated);
        Mockito.verify(boxRepo,Mockito.times(1)).findById(boxIdCreated);
        Assertions.assertEquals(box.getId(),boxCreated.getId());
    }

    @Test
    void getBox_NotFindBox_ExpectedBehavior() {
        Mockito.when(boxRepo.findById(boxIdCreated)).thenReturn(Optional.ofNullable(null));
        try {
            boxServices.getBox(boxIdCreated);
            throw new RuntimeException();
        }
        catch (EntityNotFoundException ex){
            Mockito.verify(boxRepo, Mockito.times(1)).findById(boxIdCreated);
            Assertions.assertEquals(ex.getMessage(),exceptionMessage+boxIdCreated);
        }
    }

    @Test
    void updateBox_ExpectedBehavior() {
        Mockito.when(boxRepo.save(boxCreated)).thenReturn(boxUpdated);
        Mockito.when(boxRepo.findById(boxIdCreated)).thenReturn(Optional.ofNullable(boxCreated));
        boxServices.updateBox(boxIdCreated,boxUpdated);
        Mockito.verify(boxRepo,Mockito.times(1)).findById(boxIdCreated);
        Mockito.verify(boxRepo,Mockito.times(1)).save(boxCreated);
    }
}