package com.example.liga_exam.exception;

public class OrderWasCanceledException extends RuntimeException{
    public OrderWasCanceledException(String message) {
        super(message);
    }
}
