package com.example.cinema.rabbitmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqMessageHandler {
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqMessageHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public RabbitMqClientData getClientData(Message message) {
        MessageProperties properties = message.getMessageProperties();
        return new RabbitMqClientData(properties.getReplyTo(), properties.getCorrelationId());
    }

    public void sendResponseToClient(RabbitMqClientData clientData, String response) {
        String replyTo = clientData.replyTo();
        String correlationId = clientData.correlationId();
        if (replyTo != null) {
            rabbitTemplate.convertAndSend(replyTo, response, message -> {
                message.getMessageProperties().setCorrelationId(correlationId);
                return message;
            });
        }
    }
}
