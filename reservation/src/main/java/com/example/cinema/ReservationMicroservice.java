package com.example.cinema;

import com.example.cinema.dto.ReservationRequest;
import com.example.cinema.exception.reservation.TicketReservationException;
import com.example.cinema.service.ReservationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.example.cinema.rabbitmq.RabbitMqQueues.RESERVATION_QUEUE;

@SpringBootApplication
public class ReservationMicroservice {
    private final ReservationService reservationService;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ReservationMicroservice(ReservationService reservationService,
                                    RabbitTemplate rabbitTemplate) {
        this.reservationService = reservationService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ReservationMicroservice.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
        app.run(args);
    }

    private record ClientData(String replyTo, String correlationId) {}

    @RabbitListener(queues = RESERVATION_QUEUE)
    public void receiveMessage(Message message) throws IOException {
        ClientData clientData = getClientData(message);
        String reservationServiceResponse = processReservation(message);
        sendResponseToClient(clientData, reservationServiceResponse);
    }

    private ClientData getClientData(Message message) {
        MessageProperties properties = message.getMessageProperties();
        return new ClientData(properties.getReplyTo(), properties.getCorrelationId());
    }

    private String processReservation(Message message) throws UnsupportedEncodingException, JsonProcessingException {
        ReservationRequest reservationRequest = getReservationRequest(message);
        String sessionId = reservationRequest.getSessionId();
        Set<String> seats = new HashSet<>(reservationRequest.getSeats());
        try {
            reservationService.buyTicket(sessionId, seats);
            return "Reservation successful!";
        } catch (TicketReservationException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private void sendResponseToClient(ClientData clientData, String response) {
        String replyTo = clientData.replyTo();
        String correlationId = clientData.correlationId();
        if (replyTo != null) {
            rabbitTemplate.convertAndSend(replyTo, response, message -> {
                message.getMessageProperties().setCorrelationId(correlationId);
                return message;
            });
        }
    }

    private static ReservationRequest getReservationRequest(Message message) throws UnsupportedEncodingException, JsonProcessingException {
        String messageBody = new String(message.getBody(), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(messageBody, ReservationRequest.class);
    }
}
