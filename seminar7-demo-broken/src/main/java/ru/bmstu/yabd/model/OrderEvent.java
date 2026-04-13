package ru.bmstu.yabd.model;

// TODO 1: Замените класс на Java Record
// Поля: orderId (String), product (String), quantity (int), price (double)
public record OrderEvent(
        String orderId,
        String product,
        int quantity,
        double price,
        double totalAmount,
        long createdAt,
        int version
) {}