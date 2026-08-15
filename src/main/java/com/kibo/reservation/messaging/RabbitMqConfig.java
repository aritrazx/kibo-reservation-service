package com.kibo.reservation.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String EXCHANGE = "reservation.events";

    @Bean
    TopicExchange reservationExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }
}
