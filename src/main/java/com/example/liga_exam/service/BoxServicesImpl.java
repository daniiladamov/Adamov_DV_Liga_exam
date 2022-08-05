package com.example.liga_exam.service;

import com.example.liga_exam.entity.Box;
import com.example.liga_exam.repository.BoxRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoxServicesImpl implements BoxService{
    private final BoxRepo boxRepo;

    @Override
    public Long saveBox(Box box) {
        return boxRepo.save(box).getId();
    }
}
