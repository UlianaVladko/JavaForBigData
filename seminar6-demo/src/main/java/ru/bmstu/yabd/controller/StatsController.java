package ru.bmstu.yabd.controller;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.model.EnrichedOrder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StreamsBuilderFactoryBean factoryBean;

    @GetMapping("/{product}")
    public Map<String, Object> getProductCount(@PathVariable String product) {
        ReadOnlyKeyValueStore<String, Long> store =
            factoryBean.getKafkaStreams()
                .store(StoreQueryParameters.fromNameAndType(
                    "product-counts",
                    QueryableStoreTypes.keyValueStore()));

        Long count = store.get(product);
        return Map.of("product", product, "count", count != null ? count : 0L);
    }

    @GetMapping
    public Map<String, Long> getAllCounts() {
        ReadOnlyKeyValueStore<String, Long> store =
            factoryBean.getKafkaStreams()
                .store(StoreQueryParameters.fromNameAndType(
                    "product-counts",
                    QueryableStoreTypes.keyValueStore()));

        Map<String, Long> counts = new HashMap<>();
        try (var iterator = store.all()) {
            iterator.forEachRemaining(kv -> counts.put(kv.key, kv.value));
        }
        return counts;
    }

    @GetMapping("/fraud")
    public Map<String, EnrichedOrder> getFraudOrders() {

        ReadOnlyKeyValueStore<String, EnrichedOrder> store =
                factoryBean.getKafkaStreams()
                        .store(StoreQueryParameters.fromNameAndType(
                                "fraud-orders",
                                QueryableStoreTypes.keyValueStore()
                        ));

        Map<String, EnrichedOrder> result = new HashMap<>();

        try (var iterator = store.all()) {
            iterator.forEachRemaining(kv -> result.put(kv.key, kv.value));
        }

        return result;
    }

    @GetMapping("/trending")
    public Map<String, Object> getTrending() {

        ReadOnlyKeyValueStore<String, Long> store =
                factoryBean.getKafkaStreams()
                        .store(StoreQueryParameters.fromNameAndType(
                                "trending-products",
                                QueryableStoreTypes.keyValueStore()
                        ));

        Map<String, Object> result = new HashMap<>();

        try (var iterator = store.all()) {
            iterator.forEachRemaining(kv -> {
                if (kv.value > 2) { // порог "тренда"
                    result.put(kv.key, Map.of(
                            "count", kv.value,
                            "status", "becoming a trend"
                    ));
                }
            });
        }

        return result;
    }

    @GetMapping("/revenue")
    public Map<String, Double> getRevenue() {

        ReadOnlyKeyValueStore<String, Double> store =
                factoryBean.getKafkaStreams()
                        .store(StoreQueryParameters.fromNameAndType(
                                "revenue-store",
                                QueryableStoreTypes.keyValueStore()
                        ));

        Map<String, Double> result = new HashMap<>();

        try (var iterator = store.all()) {
            iterator.forEachRemaining(kv -> result.put(kv.key, kv.value));
        }

        return result;
    }
}
