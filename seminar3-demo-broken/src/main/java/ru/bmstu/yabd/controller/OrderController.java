package ru.bmstu.yabd.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.consumer.OrderConsumer;
import ru.bmstu.yabd.model.OrderEvent;
import ru.bmstu.yabd.producer.OrderProducer;

import java.util.Map;

// TODO 9: Добавьте две аннотации на класс:
//   - @RestController  — регистрирует класс как Spring-бин и включает JSON-сериализацию
//   - @RequestMapping("/api/orders") — базовый путь для всех эндпоинтов

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer producer;
    private final OrderConsumer consumer;

    // TODO 10: Добавьте @PostMapping и реализуйте метод create:
    //   - Принимает OrderEvent из тела запроса (@RequestBody)
    //   - Вызывает producer.send(event)
    //   - Возвращает ResponseEntity.ok с Map {"status": "sent", "orderId": event.orderId()}

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody OrderEvent event) {
        producer.send(event);

        return ResponseEntity.ok(Map.of(
                "status", "sent",
                "orderId", event.orderId()
        ));
    }

    // TODO 11: Добавьте @GetMapping("/stats") и реализуйте метод stats:
    //   - Возвращает ResponseEntity.ok с Map {"processedOrders": <счётчик>}
    //   - Счётчик: consumer.getProcessedCount().get()

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
                "processedOrders", consumer.getProcessedCount().get()
        ));
    }
}
