package com.example.integration;

import com.example.cinema.dto.request.PaymentRequest;
import com.example.cinema.exception.payment.AccessTokenRetrievalException;
import com.example.cinema.exception.payment.OrderRequestException;
import com.example.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
@Testcontainers
public class PaymentIntegrationTest {
    @Autowired
    private PaymentService paymentService;

    @Container
    public static final GenericContainer REDIS = new FixedHostPortGenericContainer("redis:latest")
            .withFixedExposedPort(6379, 6379);

    @Test
    public void placeAnOrder() throws AccessTokenRetrievalException, OrderRequestException {
        PaymentRequest request = new PaymentRequest(
                "127.0.0.1",
                "Payment for the order",
                "PLN",
                "100",
                List.of(new PaymentRequest.Product("Product1", "1", "100"))
        );

        String redirectionUri = paymentService.pay(request);
        assertNotNull(redirectionUri);
    }
}
