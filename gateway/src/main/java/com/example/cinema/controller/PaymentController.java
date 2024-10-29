package com.example.cinema.controller;

import com.example.cinema.dto.request.PaymentRequest;
import jakarta.validation.Valid;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.nio.charset.StandardCharsets;

@RestController
public class PaymentController {
    private final RabbitTemplate rabbitTemplate;
    private static final String PAYMENT_EXCHANGE = "paymentExchange";
    private static final String PAYMENT_ROUTING_KEY = "payment";
    private static final String SUCCESS_URL = "https://www.paypal.com";
    private static final String FAILURE_URL = "https://www.google.com";

    public PaymentController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/api/payment")
    public RedirectView pay(@Valid @RequestBody PaymentRequest request) {
        Message message = new Message("payment".getBytes(StandardCharsets.UTF_8));
        Message response = rabbitTemplate.sendAndReceive(PAYMENT_EXCHANGE, PAYMENT_ROUTING_KEY, message);
        if (response == null) {
            return new RedirectView(FAILURE_URL);
        }
        return new RedirectView(SUCCESS_URL);
    }
}
