package ru.bmstu.yabd.consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.OrderEvent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

@Service
@Slf4j
public class AnalyticsConsumer {

    @Getter
    private final AtomicInteger totalOrders = new AtomicInteger(0);

    @Getter
    private final AtomicInteger totalPayments = new AtomicInteger(0);

    @Getter
    private final DoubleAdder totalRevenue = new DoubleAdder();

    @KafkaListener(topics = "orders", groupId = "analytics-processor")
    public void listen(OrderEvent event,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {

        long delay = System.currentTimeMillis() - event.timestamp();

        if (event.paid()) {
            totalPayments.incrementAndGet();
            totalRevenue.add(event.price() * event.quantity());
        } else {
            totalOrders.incrementAndGet();
        }

        log.info("Analytics event: orderId = {} partition = {} paid = {} delay = {}ms",
                event.orderId(), partition, event.paid(), delay);

        log.info("Analytics updated: orders = {} payments = {} revenue = {}",
                totalOrders.get(), totalPayments.get(), totalRevenue.sum());
    }
}
