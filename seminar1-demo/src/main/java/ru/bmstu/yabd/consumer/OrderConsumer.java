package ru.bmstu.yabd.consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.OrderEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class OrderConsumer {

    @Getter
    private final AtomicInteger processedCount = new AtomicInteger(0);

    @KafkaListener(topics = "orders", groupId = "order-processor")
    public void listen(OrderEvent event) {
        log.info("Received from Kafka: {}", event);
        processedCount.incrementAndGet();
    }
}
