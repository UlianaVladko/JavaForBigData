package ru.bmstu.yabd.model;

import java.time.Instant;

public record OrderEvent(
    String orderId,
    String product,
    int quantity,
    double price,
    Instant timestamp,
    Status status
) {
    public enum Status {
        CREATED,
        PROCESSING,
        COMPLETED,
        CANCELLED
    }

    public OrderEvent(String orderId, String product, int quantity, double price) {
        this(orderId, product, quantity, price, Instant.now(), Status.CREATED);
    }

    public OrderEvent withStatus(Status newStatus) {
        return new OrderEvent(orderId, product, quantity, price, timestamp, newStatus);
    }
}