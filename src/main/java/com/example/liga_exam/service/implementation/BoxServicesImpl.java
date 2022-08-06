package com.example.liga_exam.service.implementation;

import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.entity.Box;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.BoxRepo;
import com.example.liga_exam.service.BoxService;
import com.example.liga_exam.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoxServicesImpl implements BoxService {
    private final BoxRepo boxRepo;
    @Value("${exception_message}")
    private String exceptionMessage;

    @Override
    public Long createBox(Box box) {
        return boxRepo.save(box).getId();
    }

    @Override
    public Box getBox(Long boxId) {
        return boxRepo.findById(boxId).orElseThrow(()->
                new EntityNotFoundException(exceptionMessage+boxId));
    }
}
