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

    // TODO 5: Объявите поле processedCount типа AtomicInteger (начальное значение 0).
    //   Добавьте аннотацию @Getter (Lombok), чтобы контроллер мог читать счётчик.

    @Getter
    private final AtomicInteger processedCount = new AtomicInteger(0);

    // TODO 6: Добавьте аннотацию @KafkaListener на метод listen:
    //   - topics = "orders"
    //   - groupId = "order-processor"
    //   Внутри метода: залогируйте полученное событие и увеличьте счётчик на 1.
    //   Подсказка: processedCount.incrementAndGet()

    @KafkaListener(topics = "orders", groupId = "order-processor")
    public void listen(OrderEvent event) {
        log.info("Order received: {}", event);
        processedCount.incrementAndGet();
    }
}
