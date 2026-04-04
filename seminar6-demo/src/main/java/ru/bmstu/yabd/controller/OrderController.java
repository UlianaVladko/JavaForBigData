package ru.bmstu.yabd.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.model.Customer;
import ru.bmstu.yabd.model.OrderEvent;
import ru.bmstu.yabd.producer.OrderProducer;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer producer;

    @PostMapping("/orders")
    public Map<String, String> createOrder(
            @RequestBody OrderEvent event,
            @RequestParam String customerId) {
        producer.sendOrder(event, customerId);
        return Map.of("orderId", event.orderId(), "customerId", customerId, "status", "sent");
    }

    @PostMapping("/customers")
    public Map<String, String> createCustomer(@RequestBody Customer customer) {
        producer.sendCustomer(customer.customerId(), customer);
        return Map.of("customerId", customer.customerId(), "status", "saved");
    }
}
