package ru.bmstu.yabd.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.model.OrderRequest;
import ru.bmstu.yabd.producer.OrderProducer;

import java.util.Map;

// TODO 4: Добавьте аннотации @RestController и @RequestMapping("/api/orders")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderProducer producer;

    // TODO 5: Создайте POST-метод для приёма заказов
    // Принимает @RequestBody OrderEvent, вызывает producer.send(event)
    // Возвращает ResponseEntity.ok(Map.of("status", "sent", "orderId", event.getOrderId()))
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest request) {

        log.info("Received order request: {}", request);

        producer.send(request);

        return ResponseEntity.ok(
                Map.of(
                        "status", "sent",
                        "orderId", request.orderId()
                )
        );
    }
}
