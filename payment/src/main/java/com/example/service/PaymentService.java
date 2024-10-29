package com.example.service;

import com.example.cinema.dto.request.PaymentRequest;
import com.example.cinema.exception.payment.AccessTokenRetrievalException;
import com.example.cinema.exception.payment.OrderRequestException;
import com.example.paymentclient.PaymentApiClient;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentApiClient paymentApiClient;

    public PaymentService(PaymentApiClient paymentApiClient) {
        this.paymentApiClient = paymentApiClient;
    }

    public String pay(PaymentRequest request) throws AccessTokenRetrievalException, OrderRequestException {
        return paymentApiClient.pay(request);
    }
}
