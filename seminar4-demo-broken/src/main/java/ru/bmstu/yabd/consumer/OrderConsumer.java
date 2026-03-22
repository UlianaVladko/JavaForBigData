package ru.bmstu.yabd.consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.OrderEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class OrderConsumer {

    // TODO 7: Добавьте поле AtomicInteger processedCount с @Getter

    @Getter
    private final AtomicInteger processedCount = new AtomicInteger(0);

    // TODO 8: Создайте метод listen(OrderEvent event) с @KafkaListener(topics="orders", groupId="order-processor")
    //         Залогируйте получение и сделайте processedCount.incrementAndGet()

    @KafkaListener(topics = "orders", groupId = "order-processor")
    public void listen(OrderEvent event) {
        log.info("Order received: {} | product={} | region={}", event.orderId(), event.product(), event.region());
        processedCount.incrementAndGet();
    }
}
