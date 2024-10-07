package org.example;

import com.example.cinema.dto.PaymentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentMicroservice {

    public static void main(String[] args) {
        SpringApplication.run(PaymentMicroservice.class, args);
    }

    @RabbitListener(queues = "paymentQueue")
    public void listen(Message message) throws JsonProcessingException {
        String messageBody = new String(message.getBody());
        PaymentRequest paymentRequest = new ObjectMapper().readValue(messageBody, PaymentRequest.class);
        System.out.println("Received message: " + paymentRequest);
    }


}