package com.example.cinema;

import com.example.cinema.exception.reservation.TicketReservationException;
import com.example.cinema.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationMicroserviceTests {
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private ReservationService reservationService;
    @InjectMocks
    private ReservationMicroservice reservationMicroservice;

    private static final String REPLY_TO = "testReply";
    private static final String CORRELATION_ID = "testCorrelationId";
    private static final String SUCCESSFUL_RESPONSE = "Reservation successful!";


    @Test
    public void shouldSendConfirmationWhenReservationIsSuccessful() throws IOException, TicketReservationException {
        Message message = mock(Message.class);
        MessageProperties messageProperties = mock(MessageProperties.class);

        when(message.getMessageProperties()).thenReturn(messageProperties);
        when(messageProperties.getReplyTo()).thenReturn(REPLY_TO);
        when(messageProperties.getCorrelationId()).thenReturn(CORRELATION_ID);
        when(message.getBody()).thenReturn("{\"sessionId\": \"1\", \"seats\": [\"1\",\"2\"]}".getBytes());

        doNothing().when(reservationService).buyTicket(any(), any());

        reservationMicroservice.receiveMessage(message);

        ArgumentCaptor<MessagePostProcessor> captor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        verify(rabbitTemplate).convertAndSend(
                eq(REPLY_TO),
                Optional.ofNullable(eq(SUCCESSFUL_RESPONSE)),
                captor.capture()
        );

        MessagePostProcessor messagePostProcessor = captor.getValue();
        Message processedMessage = messagePostProcessor.postProcessMessage(message);
        assertEquals(CORRELATION_ID, processedMessage.getMessageProperties().getCorrelationId());
    }

    @Test
    public void shouldSendErrorMessageWhenReservationFails() throws IOException, TicketReservationException {
        Message message = mock(Message.class);
        MessageProperties messageProperties = mock(MessageProperties.class);

        when(message.getMessageProperties()).thenReturn(messageProperties);
        when(messageProperties.getReplyTo()).thenReturn(REPLY_TO);
        when(messageProperties.getCorrelationId()).thenReturn(CORRELATION_ID);
        when(message.getBody()).thenReturn("{\"sessionId\": \"1\", \"seats\": [\"1\",\"2\"]}".getBytes());

        doThrow(new TicketReservationException("Error")).when(reservationService).buyTicket(any(), any());

        reservationMicroservice.receiveMessage(message);

        ArgumentCaptor<MessagePostProcessor> captor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        verify(rabbitTemplate).convertAndSend(
                eq(REPLY_TO),
                Optional.ofNullable(argThat(response -> response.toString().startsWith("ERROR"))),
                captor.capture()
        );

        MessagePostProcessor messagePostProcessor = captor.getValue();
        Message processedMessage = messagePostProcessor.postProcessMessage(message);
        assertEquals(CORRELATION_ID, processedMessage.getMessageProperties().getCorrelationId());
    }
}
