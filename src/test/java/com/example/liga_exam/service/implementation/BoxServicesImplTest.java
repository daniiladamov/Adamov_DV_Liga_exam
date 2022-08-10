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
import org.springframework.boot.test.context.SpringBootTest;

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
        boxUpdated.setId(boxIdUpdated);
        boxUpdated.setOpen(LocalTime.MIN);
        boxUpdated.setClose(LocalTime.MAX);
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
        Throwable throwable=Assertions.assertThrows(EntityNotFoundException.class, ()->
                boxServices.getBox(boxIdCreated));
        Assertions.assertNotNull(throwable);
        Mockito.verify(boxRepo, Mockito.times(1)).findById(boxIdCreated);
    }
}