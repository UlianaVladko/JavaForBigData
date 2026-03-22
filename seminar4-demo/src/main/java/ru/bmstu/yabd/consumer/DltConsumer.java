package ru.bmstu.yabd.consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class DltConsumer {

    @Getter
    private final AtomicInteger dltCount = new AtomicInteger(0);

    @KafkaListener(topics = "orders.DLT", groupId = "dlt-processor")
    public void listen(Object record) {
        log.warn("DLT message received: {}", record);
        dltCount.incrementAndGet();
    }
}
