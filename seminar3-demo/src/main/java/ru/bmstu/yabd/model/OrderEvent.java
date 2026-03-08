package ru.bmstu.yabd.model;

public record OrderEvent(
    String orderId,
    String product,
    int quantity,
    double price,
    boolean paid,
    long timestamp
) {}
