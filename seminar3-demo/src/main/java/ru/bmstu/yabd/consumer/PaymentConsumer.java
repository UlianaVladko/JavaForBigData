package ru.bmstu.yabd.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.OrderEvent;

@Service
@Slf4j
public class PaymentConsumer {

    @KafkaListener(topics = "payments", groupId = "payment-processor")
    public void listen(OrderEvent event) {
        log.info("Payment processed for: {}", event.orderId());
    }
}
