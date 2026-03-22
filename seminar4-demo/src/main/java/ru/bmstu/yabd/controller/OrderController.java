package ru.bmstu.yabd.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.consumer.DltConsumer;
import ru.bmstu.yabd.consumer.OrderConsumer;
import ru.bmstu.yabd.model.OrderEvent;
import ru.bmstu.yabd.producer.OrderProducer;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer producer;
    private final OrderConsumer consumer;
    private final DltConsumer dltConsumer;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody OrderEvent event) {
        String traceId = UUID.randomUUID().toString();
        producer.send(event, traceId);
        return ResponseEntity.ok(Map.of(
                "orderId", event.orderId(),
                "region", event.region(),
                "status", "sent",
                "traceId", traceId
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
                "processedOrders", consumer.getProcessedCount().get(),
                "dltMessages", dltConsumer.getDltCount().get()
        ));
    }
}
