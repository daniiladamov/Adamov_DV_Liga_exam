package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.Box;
import com.example.liga_exam.repository.BoxRepo;
import com.example.liga_exam.service.BoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoxServicesImpl implements BoxService {
    private final BoxRepo boxRepo;

    @Override
    public Long createBox(Box box) {
        return boxRepo.save(box).getId();
    }
}
