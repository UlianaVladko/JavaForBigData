package ru.bmstu.yabd.model;

// TODO 1: Замените этот класс на Java record с полями:
//         orderId (String), product (String), quantity (int), price (double), region (String)
public record OrderEvent(
   String orderId,
   String product,
   int quantity,
   double price,
   String region
) {}
