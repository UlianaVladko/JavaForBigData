package ru.bmstu.yabd.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.consumer.OrderConsumer;
import ru.bmstu.yabd.model.OrderEvent;
import ru.bmstu.yabd.producer.OrderProducer;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer producer;
    private final OrderConsumer consumer;
    private final Environment env;

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody OrderEvent event) {
        OrderEvent orderWithMeta = new OrderEvent(
                event.orderId(),
                event.product(),
                event.quantity(),
                event.price(),
                Instant.now(),
                OrderEvent.Status.CREATED
        );

        producer.send(orderWithMeta);

        return ResponseEntity.ok(Map.of(
                "status", "sent",
                "orderId", orderWithMeta.orderId(),
                "timestamp", orderWithMeta.timestamp().toString(),
                "orderStatus", orderWithMeta.status().name()
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
                "processedOrders", consumer.getProcessedCount().get()
        ));
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        String appName = env.getProperty("spring.application.name", "Unknown App");
        return ResponseEntity.ok(Map.of(
                "javaVersion", System.getProperty("java.version"),
                "app.Name", appName
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
