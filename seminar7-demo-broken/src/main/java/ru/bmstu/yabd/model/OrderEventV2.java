package ru.bmstu.yabd.model;

public record OrderEventV2(
        String orderId,
        String product,
        int quantity,
        double price,
        double totalAmount,
        long createdAt,
        int version,
        double discountPrice
) {}