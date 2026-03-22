package ru.bmstu.yabd.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.OrderEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void send(OrderEvent event) {
        // TODO 6: Отправьте событие в топик "orders" с ключом event.region()
        //         Используйте kafkaTemplate.send("orders", event.region(), event)
        //         Залогируйте отправку

        log.info("Sending order: {} to region: {}", event.orderId(), event.region());
        kafkaTemplate.send("orders", event.region(), event)
                .thenAccept(result -> {
                    var metadata = result.getRecordMetadata();
                    log.info("Order {} sent to partition {} offset {}",
                            event.orderId(), metadata.partition(), metadata.offset());
                })
                .exceptionally(ex -> {
                    log.error("Failed to send order {}: {}", event.orderId(), ex.getMessage());
                    return null;
                });
    }
}
