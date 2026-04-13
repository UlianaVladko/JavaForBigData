package ru.bmstu.yabd.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.OrderEvent;
import ru.bmstu.yabd.model.OrderEventV2;
import ru.bmstu.yabd.dto.OrderRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(OrderRequest req) {
        OrderEvent event = new OrderEvent(
                req.orderId(),
                req.product(),
                req.quantity(),
                req.price(),
                req.quantity() * req.price(),
                System.currentTimeMillis(),
                1
        );
        // TODO 3: Отправьте событие в топик "orders" с ключом orderId
        // Подсказка: kafkaTemplate.send("orders", event.getOrderId(), event)
        kafkaTemplate.send("orders.v1", event.orderId(), event)
        .whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent V1 order: {} to partition {}",
                        event,
                        result.getRecordMetadata().partition());
            } else {
                log.error("Failed to send order: {}", event, ex);
            }
        });

    }

    public void sendV2(OrderRequest req) {

        OrderEventV2 event = new OrderEventV2(
                req.orderId(),
                req.product(),
                req.quantity(),
                req.price(),
                req.quantity() * req.price(),
                System.currentTimeMillis(),
                2,
                req.quantity() * req.price() * 0.9
        );

        kafkaTemplate.send("orders.v2", event.orderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent V2 order: {} to partition {}",
                                event,
                                result.getRecordMetadata().partition());
                    } else {
                        log.error("Failed to send order: {}", event, ex);
                    }
                });
    }
}
