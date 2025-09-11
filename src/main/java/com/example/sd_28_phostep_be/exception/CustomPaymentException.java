package com.example.sd_28_phostep_be.exception;

public class CustomPaymentException extends RuntimeException {
    public CustomPaymentException(String message) {
        super(message);
    }
}