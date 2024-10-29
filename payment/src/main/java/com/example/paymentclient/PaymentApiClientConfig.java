package com.example.paymentclient;

import com.example.paymentclient.payu.PayUClient;
import com.example.paymentclient.payu.PayUHttpClient;
import com.example.paymentclient.payu.PayUResponseParser;
import com.example.token.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentApiClientConfig {
    @Autowired
    private TokenCache tokenCache;
    @Autowired
    private PayUResponseParser payUResponseParser;
    @Autowired
    private PayUHttpClient payUHttpClient;
    @Value("${payment.payu.clientid}")
    private String clientId;
    @Value("${payment.payu.secret}")
    private String clientSecret;
    @Value("${payment.payu.merchantposid}")
    private String merchantPosId;

    @Bean
    public PaymentApiClient paymentApiClient() {
        return new PayUClient(tokenCache, payUHttpClient, clientId, clientSecret, merchantPosId, payUResponseParser);
    }
}