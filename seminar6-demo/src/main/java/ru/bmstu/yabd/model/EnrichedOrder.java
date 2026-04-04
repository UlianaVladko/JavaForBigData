package ru.bmstu.yabd.model;

public record EnrichedOrder(
        String orderId,
        String product,
        int quantity,
        double price,
        String customerName,
        boolean isFraud // добавили к заказу наклейку подозрительный или нормас
) {}
