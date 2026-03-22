package ru.bmstu.yabd.model;

public record OrderEvent(String orderId, String product, int quantity, double price) {}
