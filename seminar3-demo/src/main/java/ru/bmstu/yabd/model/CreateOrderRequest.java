package ru.bmstu.yabd.model;

public record CreateOrderRequest(
        String orderId,
        String product,
        int quantity,
        double price,
        boolean paid
) {}
