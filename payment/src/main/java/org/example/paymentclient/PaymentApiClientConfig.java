package org.example.paymentclient;

import org.example.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentApiClientConfig {
    @Autowired
    private TokenCache tokenCache;

    @Value("${payment.payu.clientid}")
    private String clientId;

    @Value("${payment.payu.secret}")
    private String clientSecret;

    @Bean
    public PaymentApiClient paymentApiClient() {
        return new PayUClient(tokenCache, clientId, clientSecret);
    }
}
