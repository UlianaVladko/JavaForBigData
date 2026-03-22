package ru.bmstu.yabd.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.yabd.model.OrderEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

//    @Transactional
    public void send(OrderEvent event, String traceId) {

        log.info("Sending order: {} to region: {} | traceId={}", event.orderId(), event.region(), traceId);
        kafkaTemplate.send("orders", event.region(), event)
                .thenAccept(result -> {
                    var metadata = result.getRecordMetadata();
                    log.info("Order {} sent to partition {} offset {} | traceId={}",
                            event.orderId(), metadata.partition(), metadata.offset(), traceId);
                })
                .exceptionally(ex -> {
                    log.error("Failed to send order {}: {}", event.orderId(), ex.getMessage());
                    throw new RuntimeException(ex);
                });
    }
}
