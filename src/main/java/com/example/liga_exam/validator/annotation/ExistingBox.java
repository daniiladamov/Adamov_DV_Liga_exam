package com.example.liga_exam.validator.annotation;

import com.example.liga_exam.validator.ExistingBoxValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistingBoxValidator.class)
public @interface ExistingBox {
    String message() default "указаного бокса не существует";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
