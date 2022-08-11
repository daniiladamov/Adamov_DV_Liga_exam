package com.example.liga_exam.validator;

import com.example.liga_exam.entity.Box;
import com.example.liga_exam.repository.BoxRepo;
import com.example.liga_exam.validator.annotation.ExistingBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExistingBoxValidator implements ConstraintValidator<ExistingBox, Long> {
    private final BoxRepo boxRepo;

    @Override
    public void initialize(ExistingBox constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(@Valid Long boxId, ConstraintValidatorContext context) {
        if (Objects.isNull(boxId))
            return true;
        Optional<Box> box = boxRepo.findById(boxId);
        if (box.isPresent())
            return true;
        else
            return false;
    }
}
