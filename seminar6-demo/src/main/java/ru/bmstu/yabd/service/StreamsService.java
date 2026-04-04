package ru.bmstu.yabd.service;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

@Service
public class StreamsService {

    private final StreamsBuilderFactoryBean factoryBean;

    public StreamsService(StreamsBuilderFactoryBean factoryBean) {
        this.factoryBean = factoryBean;
    }

    public KafkaStreams getKafkaStreams() {
        return factoryBean.getKafkaStreams();
    }
}