package ru.bmstu.yabd.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.consumer.DltConsumer;
import ru.bmstu.yabd.consumer.OrderConsumer;
import ru.bmstu.yabd.model.OrderEvent;
import ru.bmstu.yabd.producer.OrderProducer;

import java.util.Map;

// TODO 11: Добавьте @RestController и @RequestMapping("/api/orders")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer producer;
    private final OrderConsumer consumer;
    private final DltConsumer dltConsumer;

    // TODO 12: Создайте @PostMapping метод create(@RequestBody OrderEvent event)
    //          Вызовите producer.send(event) и верните Map с orderId, region, status

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody OrderEvent event) {
        producer.send(event);
        return ResponseEntity.ok(Map.of(
                "orderId", event.orderId(),
                "region", event.region(),
                "status", "sent"
        ));
    }

    // TODO 13: Создайте @GetMapping("/stats") метод stats()
    //          Верните Map с processedOrders и dltMessages

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
                "processedOrders", consumer.getProcessedCount().get(),
                "dltMessages", dltConsumer.getDltCount().get()
        ));
    }
}
