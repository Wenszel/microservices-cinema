package com.example.cinema.exception.payment;

public class AccessTokenRetrievalException extends Exception {
    public AccessTokenRetrievalException() {
        super("Failed to retrieve access token.");
    }
}
