package com.example.paymentclient;

import com.example.cinema.dto.request.PaymentRequest;
import com.example.cinema.exception.payment.AccessTokenRetrievalException;
import com.example.cinema.exception.payment.OrderRequestException;
import com.example.token.AccessToken;

public interface PaymentApiClient {
    String pay(PaymentRequest paymentRequest) throws AccessTokenRetrievalException, OrderRequestException;
    AccessToken getAccessToken() throws AccessTokenRetrievalException;
}
