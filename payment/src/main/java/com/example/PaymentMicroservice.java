package org.example;

import com.example.cinema.dto.request.PaymentRequest;
import com.example.cinema.exception.payment.AccessTokenRetrievalException;
import com.example.cinema.rabbitmq.RabbitMqClientData;
import com.example.cinema.rabbitmq.RabbitMqMessageHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.service.PaymentService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

import static com.example.cinema.rabbitmq.RabbitMqConfig.PAYMENT_QUEUE;

@SpringBootApplication
public class PaymentMicroservice {
    private final PaymentService paymentService;
    private final RabbitMqMessageHandler rabbitMqMessageHandler;

    public PaymentMicroservice(PaymentService paymentService,
                               RabbitMqMessageHandler rabbitMqMessageHandler) {
        this.paymentService = paymentService;
        this.rabbitMqMessageHandler = rabbitMqMessageHandler;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PaymentMicroservice.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8082"));
        app.run(args);
    }

    @RabbitListener(queues = PAYMENT_QUEUE)
    public void listen(Message message) throws JsonProcessingException, AccessTokenRetrievalException {
        String messageBody = new String(message.getBody());
        PaymentRequest paymentRequest = new ObjectMapper().readValue(messageBody, PaymentRequest.class);
        String redirectionUri = paymentService.pay(paymentRequest);
        RabbitMqClientData clientData = rabbitMqMessageHandler.getClientData(message);
        rabbitMqMessageHandler.sendResponseToClient(clientData, redirectionUri);
    }
}