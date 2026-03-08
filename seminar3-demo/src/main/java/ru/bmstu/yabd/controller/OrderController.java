package ru.bmstu.yabd.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.consumer.OrderConsumer;
import ru.bmstu.yabd.consumer.PaymentConsumer;
import ru.bmstu.yabd.model.CreateOrderRequest;
import ru.bmstu.yabd.model.OrderEvent;
import ru.bmstu.yabd.producer.OrderProducer;
import ru.bmstu.yabd.consumer.AnalyticsConsumer;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer producer;
    private final OrderConsumer orderConsumer;
    private final PaymentConsumer paymentConsumer;
    private final AnalyticsConsumer analyticsConsumer;

    @PostMapping("/orders")
    public ResponseEntity<Map<String, String>> create(@RequestBody CreateOrderRequest request) {

        OrderEvent event = new OrderEvent(
                request.orderId(),
                request.product(),
                request.quantity(),
                request.price(),
                request.paid(),
                System.currentTimeMillis()
        );

        producer.send(event);
        return ResponseEntity.ok(Map.of(
                "status", "sent",
                "orderId", request.orderId()
        ));
    }

    @GetMapping("/orders/stats")
    public ResponseEntity<Map<String, Object>> ordersStats() {
        return ResponseEntity.ok(Map.of(
                "processedOrders", orderConsumer.getProcessedCount().get()
        ));
    }

    @GetMapping("/payments/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
                "processedPayments", paymentConsumer.getProcessedPayments().get()
        ));
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> analytics() {
        return ResponseEntity.ok(Map.of(
                "totalOrders", analyticsConsumer.getTotalOrders().get(),
                "totalPayments", analyticsConsumer.getTotalPayments().get(),
                "totalRevenue", analyticsConsumer.getTotalRevenue().sum()
        ));
    }
}
