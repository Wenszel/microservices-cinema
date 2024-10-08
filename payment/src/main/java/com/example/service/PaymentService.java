package org.example.service;

import com.example.cinema.dto.request.PaymentRequest;
import com.example.cinema.exception.payment.AccessTokenRetrievalException;
import org.example.paymentclient.PaymentApiClient;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentApiClient paymentApiClient;

    public PaymentService(PaymentApiClient paymentApiClient) {
        this.paymentApiClient = paymentApiClient;
    }

    public String pay(PaymentRequest request) throws AccessTokenRetrievalException {
        return paymentApiClient.pay(request);
    }
}
