package ru.bmstu.yabd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// TODO 1: Добавьте аннотацию @EnableKafkaStreams
//         import org.springframework.kafka.annotation.EnableKafkaStreams;

import org.springframework.kafka.annotation.EnableKafkaStreams;

@EnableKafkaStreams
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
