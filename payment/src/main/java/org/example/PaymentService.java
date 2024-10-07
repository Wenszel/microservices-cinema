package org.example;

import com.example.cinema.dto.PaymentRequest;
import org.example.paymentclient.PaymentApiClient;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final TokenCache tokenCache;
    private final PaymentApiClient paymentApiClient;

    public PaymentService(TokenCache tokenCache, PaymentApiClient paymentApiClient) {
        this.tokenCache = tokenCache;
        this.paymentApiClient = paymentApiClient;
    }

    public void pay(PaymentRequest request) {
        String token = tokenCache.getToken()
                .orElse(paymentApiClient.getAccessToken());




        // payment logic
    }
}
