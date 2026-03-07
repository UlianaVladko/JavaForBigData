package ru.bmstu.yabd.model;

// TODO 1: Замените этот class на record.
//   Record должен содержать поля: orderId (String), product (String),
//   quantity (int), price (double).
//
//   Подсказка:
//   public record OrderEvent(String orderId, String product, int quantity, double price) {}
//
//   Зачем record? Компилятор автоматически генерирует конструктор, геттеры,
//   equals(), hashCode() и toString() — без Lombok и без boilerplate-кода.

public record OrderEvent(
        String orderId,
        String product,
        int quantity,
        double price
) {}
