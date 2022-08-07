package com.example.liga_exam.exception;

public class OrderWasDoneException extends RuntimeException{
    public OrderWasDoneException(String message) {
        super(message);
    }
}
