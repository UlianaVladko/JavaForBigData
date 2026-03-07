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
        // TODO 4: Залогируйте отправку (log.info) и отправьте событие в топик "orders".
        //   Ключ сообщения — event.orderId(), значение — event.
        //   Подсказка: kafkaTemplate.send("orders", event.orderId(), event)

        log.info("Sending order to Kafka: {}", event);
        kafkaTemplate.send("orders", event.orderId(), event);
    }
}
