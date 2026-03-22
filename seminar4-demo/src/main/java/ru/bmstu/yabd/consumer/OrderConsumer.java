package ru.bmstu.yabd.consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.OrderEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class OrderConsumer {

    @Getter
    private final AtomicInteger processedCount = new AtomicInteger(0);

    // дедупликация
    private final Set<String> processedIds = ConcurrentHashMap.newKeySet();

    @KafkaListener(topics = "orders", groupId = "order-processor")
    public void listen(OrderEvent event) {

        // идемпотентность
        if (!processedIds.add(event.orderId())) {
            log.warn("Duplicate order ignored: {}", event.orderId());
            return;
        }

        try {
            log.info("Processing order: {} | product={} | region={}",
                    event.orderId(), event.product(), event.region());

            processedCount.incrementAndGet();

        } catch (Exception e) {
            log.error("Error processing order {}", event.orderId(), e);
            throw e;
        }
    }
}
