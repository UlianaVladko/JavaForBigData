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
        String key = event.orderId();
        if (!event.paid()) {
            log.info("Sending ORDER event to Kafka | key = {} | event = {}", key, event);
        } else {
            log.info("Sending PAYMENT event to Kafka | key = {} | event = {}", key, event);
        }
        kafkaTemplate.send("orders", key, event);
    }
}
