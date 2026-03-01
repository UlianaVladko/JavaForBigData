package ru.bmstu.yabd.service;

import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.Order;
import ru.bmstu.yabd.model.OrderStatus;
import ru.bmstu.yabd.dto.CreateOrderRequest;
import ru.bmstu.yabd.dto.UpdateOrderRequest;
import ru.bmstu.yabd.dto.OrderStatsResponse;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final Map<String, Order> store = new ConcurrentHashMap<>();
    private final BlockingQueue<Order> queue = new LinkedBlockingQueue<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @PostConstruct
    private void startProcessing() {
        executor.submit(() -> {
            while (true) {
                try {
                    Order order = queue.take();
                    updateStatus(order.id(), OrderStatus.PROCESSING);
                    Thread.sleep(5000); // имитация обработки
                    updateStatus(order.id(), OrderStatus.COMPLETED);
                } catch (InterruptedException ignored) {}
            }
        });
    }

    public Order create(CreateOrderRequest request) {
        String id = UUID.randomUUID().toString();
        Order order = new Order(id, request.product(), request.quantity(), request.price(), OrderStatus.NEW);
        store.put(id, order);
        queue.offer(order);
        return order;
    }

    public Order findById(String id) {
        Order order = store.get(id);
        if (order == null) {
            throw new NoSuchElementException("Order not found");
        }
        return order;
    }

    public Collection<Order> findAll() {
        return store.values();
    }

    public Collection<Order> findByStatus(OrderStatus status) {
        return store.values().stream()
                .filter(o -> o.status() == status)
                .collect(Collectors.toList());
    }

    public Order update(String id, UpdateOrderRequest request) {
        Order old = findById(id);
        OrderStatus status = request.status() != null ? OrderStatus.valueOf(request.status()) : old.status();
        Order updated = new Order(id, request.product(), request.quantity(), request.price(), status);
        store.put(id, updated);
        return updated;
    }

    public void delete(String id) {
        if (store.remove(id) == null) {
            throw new NoSuchElementException("Order not found");
        }
    }

    public OrderStatsResponse stats() {
        long newOrders = store.values().stream()
                .filter(o -> o.status() == OrderStatus.NEW).count();
        long completedOrders = store.values().stream()
                .filter(o -> o.status() == OrderStatus.COMPLETED).count();
        double total = store.values().stream()
                .mapToDouble(o -> o.price() * o.quantity()).sum();
        return new OrderStatsResponse(store.size(), total, newOrders, completedOrders);
    }

    private void updateStatus(String id, OrderStatus status) {
        Order old = store.get(id);
        if (old != null) {
            store.put(id, new Order(old.id(), old.product(), old.quantity(), old.price(), status));
        }
    }
}
