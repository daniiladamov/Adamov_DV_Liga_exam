package com.example.liga_exam.validator;

import com.example.liga_exam.entity.User;
import com.example.liga_exam.repository.UserRepo;
import com.example.liga_exam.validator.annotation.UniqueUsername;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    private final UserRepo userRepo;

    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(@Valid String username, ConstraintValidatorContext context) {
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isPresent())
            return false;
        else
            return true;
    }
}
