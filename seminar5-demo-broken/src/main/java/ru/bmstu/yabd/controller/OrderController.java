package ru.bmstu.yabd.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.model.OrderEvent;
import ru.bmstu.yabd.producer.OrderProducer;

import java.util.Map;

// TODO 10: Добавьте @RestController и @RequestMapping("/api/orders")

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer producer;

    // TODO 11: Создайте @PostMapping метод create(@RequestBody OrderEvent event)
    //          Вызовите producer.send(event)
    //          Верните Map.of("orderId", event.orderId(), "status", "sent")

    @PostMapping
    public Map<String, String> create(@RequestBody OrderEvent event) {
        producer.send(event);
        return Map.of("orderId", event.orderId(), "status", "sent");
    }
}
