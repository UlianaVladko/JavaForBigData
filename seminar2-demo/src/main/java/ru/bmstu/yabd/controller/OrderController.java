package ru.bmstu.yabd.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.dto.CreateOrderRequest;
import ru.bmstu.yabd.dto.OrderStatsResponse;
import ru.bmstu.yabd.dto.UpdateOrderRequest;
import ru.bmstu.yabd.model.Order;
import ru.bmstu.yabd.model.OrderStatus;
import ru.bmstu.yabd.service.OrderService;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody CreateOrderRequest request) {
        Order created = service.create(request);
        return ResponseEntity
                .created(URI.create("/api/orders/" + created.id()))
                .body(created);
    }

    @GetMapping
    public Collection<Order> list(@RequestParam(required = false) OrderStatus status) {
        if (status != null) return service.findByStatus(status);
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable String id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Order update(@PathVariable String id, @Valid @RequestBody UpdateOrderRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public OrderStatsResponse stats() {
        return service.stats();
    }
}
