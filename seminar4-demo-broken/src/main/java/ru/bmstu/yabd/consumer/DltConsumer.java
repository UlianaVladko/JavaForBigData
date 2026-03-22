package ru.bmstu.yabd.consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class DltConsumer {

    // TODO 9: Добавьте поле AtomicInteger dltCount с @Getter

    @Getter
    private final AtomicInteger dltCount = new AtomicInteger(0);

    // TODO 10: Создайте метод listen(Object record) с @KafkaListener(topics="orders.DLT", groupId="dlt-processor")
    //          Залогируйте DLT-сообщение и сделайте dltCount.incrementAndGet()

    @KafkaListener(topics = "orders.DLT", groupId = "dlt-processor")
    public void listen(Object record) {
        log.warn("DLT message received: {}", record);
        dltCount.incrementAndGet();
    }
}
