package com.example.cinema;

import com.example.cinema.dto.request.ReservationRequest;
import com.example.cinema.exception.reservation.TicketReservationException;
import com.example.cinema.rabbitmq.RabbitMqClientData;
import com.example.cinema.rabbitmq.RabbitMqMessageHandler;
import com.example.cinema.service.ReservationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.example.cinema.rabbitmq.RabbitMqConfig.RESERVATION_QUEUE;

@SpringBootApplication
public class ReservationMicroservice {
    private final ReservationService reservationService;
    private final RabbitMqMessageHandler rabbitMqMessageHandler;

    @Autowired
    public ReservationMicroservice(ReservationService reservationService,
                                    RabbitMqMessageHandler rabbitMqMessageHandler) {
        this.reservationService = reservationService;
        this.rabbitMqMessageHandler = rabbitMqMessageHandler;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ReservationMicroservice.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
        app.run(args);
    }

    @RabbitListener(queues = RESERVATION_QUEUE)
    public void receiveMessage(Message message) throws IOException {
        RabbitMqClientData clientData = rabbitMqMessageHandler.getClientData(message);
        String reservationServiceResponse = processReservation(message);
        rabbitMqMessageHandler.sendResponseToClient(clientData, reservationServiceResponse);
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

    private static ReservationRequest getReservationRequest(Message message) throws UnsupportedEncodingException, JsonProcessingException {
        String messageBody = new String(message.getBody(), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(messageBody, ReservationRequest.class);
    }
}
