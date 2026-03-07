package ru.bmstu.yabd.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.consumer.OrderConsumer;
import ru.bmstu.yabd.model.OrderEvent;
import ru.bmstu.yabd.producer.OrderProducer;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer producer;
    private final OrderConsumer consumer;

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody OrderEvent event) {
        producer.send(event);
        producer.sendPayment(event);
        return ResponseEntity.ok(Map.of(
                "status", "sent",
                "orderId", event.orderId()
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
                "processedOrders", consumer.getProcessedCount().get()
        ));
    }
}
