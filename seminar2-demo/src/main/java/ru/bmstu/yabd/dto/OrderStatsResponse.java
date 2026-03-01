package ru.bmstu.yabd.dto;

public record OrderStatsResponse(
        long totalOrders,
        double totalSum,
        long newOrders,
        long completedOrders
) {}