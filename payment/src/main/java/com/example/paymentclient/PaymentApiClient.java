package org.example.paymentclient;

import com.example.cinema.dto.request.PaymentRequest;
import com.example.cinema.exception.payment.AccessTokenRetrievalException;
import org.example.token.AccessToken;

public interface PaymentApiClient {
    String pay(PaymentRequest paymentRequest) throws AccessTokenRetrievalException;
    AccessToken getAccessToken() throws AccessTokenRetrievalException;
}
