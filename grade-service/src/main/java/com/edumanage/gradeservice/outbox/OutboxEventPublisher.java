package com.edumanage.gradeservice.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private static final int BATCH_SIZE = 100;

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // fixedRate (not fixedDelay) — next run starts 1s after previous start, not after it ends
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void publishPendingEvents() {
        Slice<OutboxEvent> pending = outboxEventRepository
                .findByPublishedFalseOrderByCreatedAtAsc(PageRequest.of(0, BATCH_SIZE));

        for (OutboxEvent event : pending) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload());
                event.setPublished(true);
                event.setPublishedAt(LocalDateTime.now());
                outboxEventRepository.save(event);
                log.debug("Published outbox event id={} topic={}", event.getId(), event.getTopic());
            } catch (Exception e) {
                log.error("Failed to publish outbox event id={} topic={}: {}",
                        event.getId(), event.getTopic(), e.getMessage());
                // Leave published=false — next cycle will retry
            }
        }

        if (pending.hasContent()) {
            log.info("Outbox cycle: published {}/{} events", pending.getNumberOfElements(), BATCH_SIZE);
        }
    }
}
