package com.kibo.reservation.messaging;

import com.kibo.reservation.entity.OutboxEvent;
import com.kibo.reservation.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxEventRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPending() {
        for (OutboxEvent event : repository.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc()) {
            try {
                rabbitTemplate.convertAndSend(
                        RabbitMqConfig.EXCHANGE,
                        event.getEventType(),
                        event.getPayload()
                );
                event.setPublishedAt(Instant.now());
                repository.save(event);
            } catch (Exception ignored) {
                // Leave unpublished so the next poll retries it.
            }
        }
    }
}
