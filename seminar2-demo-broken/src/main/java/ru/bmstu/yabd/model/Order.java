package ru.bmstu.yabd.model;

public record Order(
        String id,
        String product,
        int quantity,
        double price
) {}

// TODO 1: Объявите record Order с полями:
//   - String id
//   - String product
//   - int quantity
//   - double price
//
// Подсказка: record — это специальный тип класса в Java 16+,
// который автоматически генерирует конструктор, геттеры, equals, hashCode, toString.
// Синтаксис: public record ИмяКласса(тип поле1, тип поле2, ...) {}

