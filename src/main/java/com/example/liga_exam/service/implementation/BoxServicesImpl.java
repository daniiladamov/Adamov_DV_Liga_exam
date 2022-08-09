package com.example.liga_exam.service.implementation;

import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.entity.Box;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.BoxRepo;
import com.example.liga_exam.service.BoxService;
import com.example.liga_exam.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxServicesImpl implements BoxService {
    @Value("${company_open}")
    @Setter
    private Integer companyOpen;
    @Value("${company_close}")
    @Setter
    private Integer companyClose;
    private final BoxRepo boxRepo;
    @Value("${exception_message}")
    private String exceptionMessage;

    @Override
    @Transactional
    public Long createBox(Box box) {
        LocalTime open=LocalTime.of(companyOpen, 0);
        LocalTime close=LocalTime.of(companyClose, 0);
        if (open.isAfter(box.getOpen()))
            box.setOpen(open);
        if (close.isBefore(box.getClose()))
            box.setClose(close);
        return boxRepo.save(box).getId();
    }

    @Override
    public Box getBox(Long boxId) {
        return boxRepo.findById(boxId).orElseThrow(()->
                new EntityNotFoundException(exceptionMessage+boxId));
    }

    @Override
    @Transactional
    public void updateBox(Long id, Box updateBox) {
        Box box=getBox(id);
        box.setRatio(updateBox.getRatio());
        box.setOpen(updateBox.getOpen());
        box.setClose(updateBox.getClose());
        createBox(box);
    }

}
