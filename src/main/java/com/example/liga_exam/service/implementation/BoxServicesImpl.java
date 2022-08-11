package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.Box;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.BoxRepo;
import com.example.liga_exam.service.BoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.Objects;

import static com.example.liga_exam.util.ExceptionMessage.INVALID_INTERVAL;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxServicesImpl implements BoxService {
    @Value("${company_open}")
    private Integer companyOpen;
    @Value("${company_close}")
    private Integer companyClose;
    private final BoxRepo boxRepo;
    @Value("${exception_message}")
    private String exceptionMessage;

    @Override
    @Transactional
    public Long createBox(Box box) {
        LocalTime open = LocalTime.of(companyOpen, 0);
        LocalTime close = LocalTime.of(companyClose, 0);
        if (open.isAfter(box.getOpen()))
            box.setOpen(open);
        if (close.isBefore(box.getClose()))
            box.setClose(close);
        if (box.getOpen().isAfter(box.getClose()))
            throw new DateTimeException(INVALID_INTERVAL.getMessage());
        return boxRepo.save(box).getId();
    }

    @Override
    public Box getBox(Long boxId) {
        if (Objects.isNull(boxId))
            return null;
        return boxRepo.findById(boxId).orElseThrow(() ->
                new EntityNotFoundException(exceptionMessage + boxId));
    }

    @Override
    @Transactional
    public void updateBox(Long id, Box updateBox) {
        Box box = getBox(id);
        box.setRatio(updateBox.getRatio());
        box.setOpen(updateBox.getOpen());
        box.setClose(updateBox.getClose());
        createBox(box);
    }
}
