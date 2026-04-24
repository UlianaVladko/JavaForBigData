package ru.bmstu.yabd.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.OrderEvent;
import ru.bmstu.yabd.model.OrderRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void send(OrderRequest request) {
        // TODO 3: Отправьте событие в топик "orders" с ключом orderId
        OrderEvent event = new OrderEvent(
                request.orderId(),
                request.product(),
                request.quantity(),
                request.price(),
                System.currentTimeMillis()
        );

        log.info("Sending order event: {}", event);

        kafkaTemplate.send("orders", event.orderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send order {}", event.orderId(), ex);
                    } else {
                        log.info("Order {} sent to partition {}",
                                event.orderId(),
                                result.getRecordMetadata().partition());
                    }
                });
    }
}
