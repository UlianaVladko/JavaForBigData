package ru.bmstu.yabd.consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.OrderEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class PaymentConsumer {

    @Getter
    private final AtomicInteger processedPayments = new AtomicInteger(0);

    @KafkaListener(topics = "orders", groupId = "payment-processor")
    public void listen(OrderEvent event,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        if (event.paid()) {
            processedPayments.incrementAndGet();
            log.info("Payment processed: orderId = {} partition = {}", event.orderId(), partition);
        }
    }
}
