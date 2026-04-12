package ru.bmstu.yabd.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.dto.OrderRequest;
import ru.bmstu.yabd.producer.OrderProducer;

import java.util.Map;

// TODO 4: Добавьте аннотации @RestController и @RequestMapping("/api/orders")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer producer;

    // TODO 5: Создайте метод POST для создания заказа
    // Метод принимает @RequestBody OrderEvent event
    // Вызывает producer.send(event)
    // Возвращает ResponseEntity.ok(Map.of("status", "sent", "orderId", event.getOrderId()))
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderRequest request) {
        producer.send(request);
        return ResponseEntity.ok(Map.of(
                "status", "sent",
                "orderId", request.orderId()
        ));
    }

}
