package org.example.paymentclient;

import com.example.cinema.dto.PaymentRequest;

public interface PaymentApiClient {
    void pay(PaymentRequest paymentRequest);
    String getAccessToken();
}
