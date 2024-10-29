package com.example.cinema.exception.payment;

public class OrderRequestException extends Exception {
    public OrderRequestException(String message) {
        super("Order request failed: " + message);
    }
    public OrderRequestException() {
        super("Order request failed");
    }
}
