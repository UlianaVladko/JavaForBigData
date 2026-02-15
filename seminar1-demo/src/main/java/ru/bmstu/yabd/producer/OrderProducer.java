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
        log.info("Sending to Kafka: {}", event);
        kafkaTemplate.send("orders", event.orderId(), event);
    }

    public void send(String orderId, String product, int quantity, double price) {
        OrderEvent order = new OrderEvent(orderId, product, quantity, price);
        send(order);
    }
}
