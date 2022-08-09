package com.example.liga_exam.util;

import com.auth0.jwt.exceptions.IncorrectClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.liga_exam.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.management.relation.InvalidRoleValueException;
import javax.naming.AuthenticationException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.DateTimeException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.ControllerAdvice
@RestController
@Slf4j
public class ControllerAdvice extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.error(errors.toString());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DiscountException.class, EntityNotFoundException.class, FreeBoxesNotFound.class,
            OrderWasCanceledException.class, OrderWasDoneException.class,RepeatedArrivedException.class,
            InvalidRoleValueException.class, OrderConfirmException.class, IntersectionOrderTimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String expectedExceptions(Exception exception){
        log.error(exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler(DateTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String expectedExceptions(DateTimeException exception){
        log.error(exception.getMessage());
        return exception.getMessage();
    }
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String validateFallResponse(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        return constraintViolations.stream().map(e -> e.getPropertyPath().toString() + ":"
                + e.getMessage()).collect(Collectors.joining("\n"));

    }
    @ExceptionHandler({JWTVerificationException.class, IncorrectClaimException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String verificationJwtFalls(){
        return "JWT-токен не прошел верификацию на сервере приложения";
    }
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String operationNotAccess() {
        return "Ошибка авторизации. Данная операция не доступна пользователю";
    }
}
