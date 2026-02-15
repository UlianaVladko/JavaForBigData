package ru.bmstu.yabd.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> hello(@RequestParam(defaultValue = "World") String name) {
        return Map.of("message", "Hello, " + name + "!");
    }

    @GetMapping("/info-hello")
    public Map<String, Object> info() {
        return Map.of(
                "javaVersion", System.getProperty("java.version"),
                "appName", "Hello Demo App"
        );
    }
}
