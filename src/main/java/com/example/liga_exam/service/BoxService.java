package com.example.liga_exam.service;

import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.entity.Box;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface BoxService {
    Long createBox(Box box);

    Box getBox(Long boxId);
}
