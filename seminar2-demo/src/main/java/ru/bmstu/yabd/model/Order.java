package ru.bmstu.yabd.model;

public record Order(
    String id,
    String product,
    int quantity,
    double price,
    OrderStatus status
) {}
